import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Manages the library's physical and digital media inventory,
 * item lookups, and circulation tracking.
 */
public class LibraryCatalog {
    private final String libraryName;
    private final List<LibraryItem> inventory;
    private final List<LoanTransaction> activeLoans;
    private final List<LoanTransaction> completedHistory;

    public LibraryCatalog(String libraryName) {
        this.libraryName = (libraryName == null || libraryName.trim().isEmpty())
                ? "Metropolis Central Public Library" : libraryName.trim();
        this.inventory = new ArrayList<>();
        this.activeLoans = new ArrayList<>();
        this.completedHistory = new ArrayList<>();
    }

    public String getLibraryName() {
        return libraryName;
    }

    /**
     * Registers a new item into the library catalog.
     */
    public void addItem(LibraryItem item) {
        if (item == null) {
            throw new IllegalArgumentException("Cannot add a null item to catalog.");
        }
        for (LibraryItem existing : inventory) {
            if (existing.getItemId().equalsIgnoreCase(item.getItemId())) {
                throw new IllegalArgumentException("Item with ID '" + item.getItemId() + "' already exists.");
            }
        }
        inventory.add(item);
    }

    /**
     * Finds an item by exact unique ID (case-insensitive).
     */
    public LibraryItem findItemById(String itemId) {
        if (itemId == null) {
            return null;
        }
        String cleanId = itemId.trim().toUpperCase();
        for (LibraryItem item : inventory) {
            if (item.getItemId().equalsIgnoreCase(cleanId)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Searches items whose title contains the query string.
     */
    public List<LibraryItem> searchByTitle(String query) {
        if (query == null || query.trim().isEmpty()) {
            return Collections.emptyList();
        }
        String cleanQuery = query.trim().toLowerCase();
        List<LibraryItem> matches = new ArrayList<>();
        for (LibraryItem item : inventory) {
            if (item.getTitle().toLowerCase().contains(cleanQuery)) {
                matches.add(item);
            }
        }
        return matches;
    }

    public List<LibraryItem> getAllItems() {
        return Collections.unmodifiableList(inventory);
    }

    public int getTotalItemCount() {
        return inventory.size();
    }

    public int getAvailableItemCount() {
        int count = 0;
        for (LibraryItem item : inventory) {
            if (item.isAvailable()) {
                count++;
            }
        }
        return count;
    }

    public int getLoanedItemCount() {
        return getTotalItemCount() - getAvailableItemCount();
    }

    /**
     * Counts how many items a specific patron currently has checked out.
     */
    public int getActiveLoanCountForMember(String memberId) {
        if (memberId == null) {
            return 0;
        }
        int count = 0;
        for (LoanTransaction tx : activeLoans) {
            if (tx.getBorrowerId().equalsIgnoreCase(memberId.trim())) {
                count++;
            }
        }
        return count;
    }

    /**
     * Processes checkout of a catalog item with member tier quota and availability checks.
     * @param itemId barcode or catalog ID
     * @param borrowerId patron unique ID
     * @param borrowerName patron name
     * @param tier patron membership tier
     * @param requestedDays duration in days
     * @return generated active LoanTransaction
     * @throws ItemUnavailableException if item is already borrowed
     * @throws BorrowLimitExceededException if patron exceeds allowed checkout quota
     * @throws InvalidDurationException if requested days exceed limits or are non-positive
     */
    public LoanTransaction checkoutItem(String itemId, String borrowerId, String borrowerName,
                                        MembershipTier tier, int requestedDays)
            throws ItemUnavailableException, BorrowLimitExceededException, InvalidDurationException {
        LibraryItem item = findItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item with ID '" + itemId + "' not found in catalog.");
        }

        MembershipTier memberTier = (tier == null) ? MembershipTier.REGULAR : tier;
        int activeLoansCount = getActiveLoanCountForMember(borrowerId);

        if (activeLoansCount >= memberTier.getMaxBorrowLimit()) {
            throw new BorrowLimitExceededException(borrowerId, borrowerName, activeLoansCount, memberTier.getMaxBorrowLimit());
        }

        // Tier multiplier allows extended loan periods for scholars and faculty
        int allowableDays = (int) Math.round(item.getMaxLoanDays() * memberTier.getMaxDaysMultiplier());
        if (requestedDays > allowableDays) {
            throw new InvalidDurationException(requestedDays,
                    String.format("Requested %d days exceeds tier allowable limit of %d days for %s (%s).",
                            requestedDays, allowableDays, item.getItemType(), memberTier.getDescription()));
        }

        // Delegate to item for state transition and base validation
        item.borrowItem(borrowerId, requestedDays);

        LoanTransaction transaction = new LoanTransaction(
                item.getItemId(), item.getTitle(), borrowerId, borrowerName, memberTier, requestedDays);
        activeLoans.add(transaction);
        return transaction;
    }

    public List<LoanTransaction> getActiveLoans() {
        return Collections.unmodifiableList(activeLoans);
    }

    /**
     * Processes return of a catalog item, updates availability, and computes overdue fines.
     * @param itemId barcode or catalog ID of the item being returned
     * @param actualDaysKept number of days the item was in borrower's possession
     * @return completed LoanTransaction with financial fine calculation
     * @throws InvalidDurationException if actualDaysKept is negative
     */
    public LoanTransaction returnItem(String itemId, int actualDaysKept) throws InvalidDurationException {
        LibraryItem item = findItemById(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Item with ID '" + itemId + "' not found in catalog.");
        }
        if (item.isAvailable()) {
            throw new IllegalStateException("Item '" + itemId + "' is already in library inventory (not on loan).");
        }

        // Locate active transaction
        LoanTransaction activeTx = null;
        for (LoanTransaction tx : activeLoans) {
            if (tx.getItemId().equalsIgnoreCase(item.getItemId())) {
                activeTx = tx;
                break;
            }
        }

        double baseFine = item.returnItem(actualDaysKept);

        if (activeTx != null) {
            activeTx.recordReturn(actualDaysKept, baseFine);
            activeLoans.remove(activeTx);
            completedHistory.add(activeTx);
            return activeTx;
        } else {
            // Fallback transaction if checkout wasn't tracked via activeLoans
            LoanTransaction manualTx = new LoanTransaction(
                    item.getItemId(), item.getTitle(), "GUEST", "Walk-in Patron", MembershipTier.REGULAR, 7);
            manualTx.recordReturn(actualDaysKept, baseFine);
            completedHistory.add(manualTx);
            return manualTx;
        }
    }

    public List<LoanTransaction> getCompletedHistory() {
        return Collections.unmodifiableList(completedHistory);
    }

    /**
     * Prints an ASCII visual shelf representation of library inventory.
     */
    public void displayShelfOverview() {
        System.out.println("\n+----------------------------------------------------------------------------------------------------+");
        System.out.printf("| %-98s |\n", libraryName.toUpperCase() + " - PHYSICAL & DIGITAL SHELF MAP");
        System.out.println("+------------+-------------------------------------+--------------+----------------------------------+");
        System.out.printf("| %-10s | %-35s | %-12s | %-32s |\n", "ITEM ID", "TITLE (YEAR)", "STATUS", "DETAILS / BORROWER");
        System.out.println("+------------+-------------------------------------+--------------+----------------------------------+");

        if (inventory.isEmpty()) {
            System.out.println("|                              No items registered in library catalog                                |");
        } else {
            for (LibraryItem item : inventory) {
                String statusStr;
                String extraInfo;
                if (item instanceof ReferenceMaterial) {
                    statusStr = "[IN-LIB]";
                    extraInfo = ((ReferenceMaterial) item).getShelfLocation();
                } else if (item.isAvailable()) {
                    statusStr = "[AVAILABLE]";
                    extraInfo = item.getItemType();
                } else {
                    statusStr = "[ON LOAN]";
                    extraInfo = "Patron: " + item.getCurrentBorrowerId();
                }

                String displayTitle = item.getTitle();
                if (displayTitle.length() > 27) {
                    displayTitle = displayTitle.substring(0, 24) + "...";
                }
                displayTitle = String.format("%s (%d)", displayTitle, item.getPublicationYear());

                if (extraInfo.length() > 32) {
                    extraInfo = extraInfo.substring(0, 29) + "...";
                }

                System.out.printf("| %-10s | %-35s | %-12s | %-32s |\n",
                        item.getItemId(), displayTitle, statusStr, extraInfo);
            }
        }

        System.out.println("+------------+-------------------------------------+--------------+----------------------------------+");
        System.out.printf("| Statistics: %d Total Items | %d Available / Reading Room | %d Currently Loaned Out%-18s |\n",
                getTotalItemCount(), getAvailableItemCount(), getLoanedItemCount(), "");
        System.out.println("+----------------------------------------------------------------------------------------------------+\n");
    }

    /**
     * Generates a comprehensive financial audit and circulation report.
     */
    public void generateFinancialAuditReport() {
        double totalGrossFines = 0.0;
        double totalDiscounts = 0.0;
        double totalNetFines = 0.0;

        for (LoanTransaction tx : completedHistory) {
            totalGrossFines += tx.getBaseFine();
            totalDiscounts += tx.getDiscountAmount();
            totalNetFines += tx.getNetFinePaid();
        }

        System.out.println("\n==========================================================================================");
        System.out.println("                 LIBRARY CIRCULATION & FINANCIAL AUDIT LEDGER REPORT                     ");
        System.out.println("==========================================================================================");
        System.out.printf(" Library Facility            : %s\n", libraryName);
        System.out.printf(" Total Registered Items      : %d\n", getTotalItemCount());
        System.out.printf(" Active Loans in Circulation : %d\n", activeLoans.size());
        System.out.printf(" Completed Return Records    : %d\n", completedHistory.size());
        System.out.println("------------------------------------------------------------------------------------------");
        System.out.printf(" Total Gross Fines Assessed  : $%12.2f\n", totalGrossFines);
        System.out.printf(" Total Tier Discounts Given  :-$%12.2f\n", totalDiscounts);
        System.out.printf(" Total Net Revenue Collected : $%12.2f\n", totalNetFines);
        System.out.println("==========================================================================================");
        System.out.printf("| %-10s | %-12s | %-18s | %-12s | %-10s | %-12s |\n",
                "TXN ID", "PATRON ID", "ITEM", "TIER", "OVERDUE", "FINE PAID");
        System.out.println("+------------+--------------+--------------------+--------------+------------+--------------+");

        if (completedHistory.isEmpty()) {
            System.out.println("|                      No completed return transactions on record                         |");
        } else {
            for (LoanTransaction tx : completedHistory) {
                int overdue = Math.max(0, tx.getActualDaysKept() - tx.getScheduledDays());
                String titleCut = tx.getItemTitle();
                if (titleCut.length() > 18) {
                    titleCut = titleCut.substring(0, 15) + "...";
                }
                System.out.printf("| %-10s | %-12s | %-18s | %-12s | %-10s | $%10.2f |\n",
                        tx.getTransactionId(), tx.getBorrowerId(), titleCut,
                        tx.getMemberTier().name(), overdue + " days", tx.getNetFinePaid());
            }
        }
        System.out.println("==========================================================================================\n");
    }
}

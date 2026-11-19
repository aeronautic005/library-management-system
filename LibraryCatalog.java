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
}

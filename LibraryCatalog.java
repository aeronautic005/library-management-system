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
}

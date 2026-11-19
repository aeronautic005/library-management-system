/**
 * Exception thrown when attempting to checkout an item that is currently
 * borrowed by another patron or marked unavailable.
 */
public class ItemUnavailableException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String itemId;
    private final String itemTitle;
    private final String currentBorrowerId;

    public ItemUnavailableException(String message) {
        super(message);
        this.itemId = "N/A";
        this.itemTitle = "Unknown";
        this.currentBorrowerId = "Unknown";
    }

    public ItemUnavailableException(String itemId, String itemTitle, String currentBorrowerId) {
        super(String.format("Item '%s' (%s) is unavailable. Currently checked out by patron ID '%s'.",
                itemId, itemTitle, currentBorrowerId));
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.currentBorrowerId = currentBorrowerId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }
}

/**
 * Abstract foundation representing any item cataloged in the library.
 * Implements the Borrowable interface with core lending lifecycle logic.
 */
public abstract class LibraryItem implements Borrowable {
    protected final String itemId;
    protected final String title;
    protected final int publicationYear;
    protected boolean available;
    protected String currentBorrowerId;
    protected int loanDurationDays;

    public LibraryItem(String itemId, String title, int publicationYear) {
        if (itemId == null || itemId.trim().isEmpty()) {
            throw new IllegalArgumentException("Item ID cannot be null or empty.");
        }
        if (title == null || title.trim().isEmpty()) {
            throw new IllegalArgumentException("Item title cannot be null or empty.");
        }
        this.itemId = itemId.trim().toUpperCase();
        this.title = title.trim();
        this.publicationYear = publicationYear;
        this.available = true;
        this.currentBorrowerId = null;
        this.loanDurationDays = 0;
    }

    public String getItemId() {
        return itemId;
    }

    public String getTitle() {
        return title;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    @Override
    public boolean isAvailable() {
        return available;
    }

    public String getCurrentBorrowerId() {
        return currentBorrowerId;
    }

    public int getLoanDurationDays() {
        return loanDurationDays;
    }

    @Override
    public void borrowItem(String borrowerId, int durationDays)
            throws ItemUnavailableException, InvalidDurationException {
        if (!this.available) {
            throw new ItemUnavailableException(this.itemId, this.title, this.currentBorrowerId);
        }
        if (durationDays <= 0) {
            throw new InvalidDurationException(durationDays, "Requested borrow duration must be at least 1 day.");
        }
        if (durationDays > getMaxLoanDays()) {
            throw new InvalidDurationException(durationDays,
                    String.format("Requested duration exceeds maximum allowable limit of %d days for %s.",
                            getMaxLoanDays(), getItemType()));
        }

        this.available = false;
        this.currentBorrowerId = borrowerId;
        this.loanDurationDays = durationDays;
    }

    @Override
    public double returnItem(int actualDaysKept) throws InvalidDurationException {
        if (actualDaysKept < 0) {
            throw new InvalidDurationException(actualDaysKept, "Actual days kept cannot be negative.");
        }

        int scheduledDuration = this.loanDurationDays;
        int overdueDays = Math.max(0, actualDaysKept - scheduledDuration);
        double fine = calculateOverdueFine(overdueDays);

        // Reset lending state
        this.available = true;
        this.currentBorrowerId = null;
        this.loanDurationDays = 0;

        return fine;
    }

    @Override
    public double calculateOverdueFine(int daysOverdue) {
        if (daysOverdue <= 0) {
            return 0.0;
        }
        return daysOverdue * getDailyFineRate();
    }

    /**
     * Polymorphic detailed summary of item attributes.
     */
    public abstract String getDetails();

    @Override
    public String toString() {
        return String.format("[%s] %s (%d) - Status: %s",
                itemId, title, publicationYear, available ? "AVAILABLE" : "ON LOAN (Patron: " + currentBorrowerId + ")");
    }
}

/**
 * Contract representing items that can be checked out, returned,
 * and assessed with overdue fines in the library system.
 */
public interface Borrowable {

    /**
     * Checks if the item is currently available for checkout.
     * @return true if available, false if already loaned out
     */
    boolean isAvailable();

    /**
     * Checks out the item to a borrower for a given number of days.
     * @param borrowerId unique identification of the borrowing member
     * @param durationDays requested loan period
     * @throws ItemUnavailableException if item is already borrowed
     * @throws InvalidDurationException if duration is non-positive or exceeds maximum allowable limit
     */
    void borrowItem(String borrowerId, int durationDays)
            throws ItemUnavailableException, InvalidDurationException;

    /**
     * Returns the item to the library and computes any overdue fines.
     * @param actualDaysKept the actual number of days the item was held
     * @return calculated base overdue fine (0.0 if returned on time)
     * @throws InvalidDurationException if actualDaysKept is negative
     */
    double returnItem(int actualDaysKept) throws InvalidDurationException;

    /**
     * Calculates the overdue fine for a given number of overdue days.
     * @param daysOverdue number of days past the scheduled due date
     * @return overdue fine amount
     */
    double calculateOverdueFine(int daysOverdue);

    /**
     * Gets the standard maximum allowable loan days for this item type.
     * @return allowable days
     */
    int getMaxLoanDays();

    /**
     * Gets the daily overdue fine rate for this item type.
     * @return fine per overdue day
     */
    double getDailyFineRate();

    /**
     * Gets the descriptive media category.
     * @return category string
     */
    String getItemType();
}

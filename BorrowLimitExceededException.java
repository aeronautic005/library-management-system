/**
 * Exception thrown when a library member attempts to borrow more items
 * than permitted by their membership tier allowance.
 */
public class BorrowLimitExceededException extends Exception {
    private static final long serialVersionUID = 1L;

    private final String memberId;
    private final String memberName;
    private final int currentLoans;
    private final int maxAllowed;

    public BorrowLimitExceededException(String message) {
        super(message);
        this.memberId = "N/A";
        this.memberName = "Unknown";
        this.currentLoans = 0;
        this.maxAllowed = 0;
    }

    public BorrowLimitExceededException(String memberId, String memberName, int currentLoans, int maxAllowed) {
        super(String.format("Borrowing limit exceeded for member '%s' (%s). Current loans: %d, Max quota allowed: %d.",
                memberId, memberName, currentLoans, maxAllowed));
        this.memberId = memberId;
        this.memberName = memberName;
        this.currentLoans = currentLoans;
        this.maxAllowed = maxAllowed;
    }

    public String getMemberId() {
        return memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public int getCurrentLoans() {
        return currentLoans;
    }

    public int getMaxAllowed() {
        return maxAllowed;
    }
}

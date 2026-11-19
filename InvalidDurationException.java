/**
 * Exception thrown when an invalid duration (e.g., non-positive, zero,
 * or exceeding allowable maximum limits) is specified for an operation.
 */
public class InvalidDurationException extends Exception {
    private static final long serialVersionUID = 1L;

    private final int invalidDays;
    private final String reason;

    public InvalidDurationException(String message) {
        super(message);
        this.invalidDays = -1;
        this.reason = message;
    }

    public InvalidDurationException(int invalidDays, String reason) {
        super(String.format("Invalid duration '%d days': %s", invalidDays, reason));
        this.invalidDays = invalidDays;
        this.reason = reason;
    }

    public int getInvalidDays() {
        return invalidDays;
    }

    public String getReason() {
        return reason;
    }
}

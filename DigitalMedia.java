/**
 * Represents digital and electronic library media (e-books, audiobooks, video streams)
 * which have shorter loan durations and digital licensing rates.
 */
public class DigitalMedia extends LibraryItem {
    public static final int MAX_DIGITAL_LOAN_DAYS = 7;
    public static final double DAILY_FINE_RATE = 2.50;

    private final String mediaFormat;
    private final double fileSizeMb;
    private final double baseLicenseFee;

    public DigitalMedia(String itemId, String title, int publicationYear,
                        String mediaFormat, double fileSizeMb, double baseLicenseFee) {
        super(itemId, title, publicationYear);
        this.mediaFormat = (mediaFormat == null || mediaFormat.trim().isEmpty()) ? "E-Book" : mediaFormat.trim();
        this.fileSizeMb = Math.max(0.0, fileSizeMb);
        this.baseLicenseFee = Math.max(0.0, baseLicenseFee);
    }

    public String getMediaFormat() {
        return mediaFormat;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    public double getBaseLicenseFee() {
        return baseLicenseFee;
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_DIGITAL_LOAN_DAYS;
    }

    @Override
    public double getDailyFineRate() {
        return DAILY_FINE_RATE;
    }

    @Override
    public String getItemType() {
        return "Digital Media (" + mediaFormat + ")";
    }

    @Override
    public double calculateOverdueFine(int daysOverdue) {
        if (daysOverdue <= 0) {
            return 0.0;
        }
        // Base overdue charge + flat license renewal fee
        return (daysOverdue * getDailyFineRate()) + (baseLicenseFee * 0.50);
    }

    @Override
    public String getDetails() {
        return String.format("Type: %s | Format: %s | Size: %.1f MB | License Fee: $%.2f | Max Loan: %d days | Fine: $%.2f/day",
                getItemType(), mediaFormat, fileSizeMb, baseLicenseFee, getMaxLoanDays(), getDailyFineRate());
    }
}

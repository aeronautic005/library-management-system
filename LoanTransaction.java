import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Model capturing loan lifecycle transactions, itemized charges,
 * membership tier discounts, and printable ASCII receipts.
 */
public class LoanTransaction {
    private static int transactionCounter = 1000;

    private final String transactionId;
    private final String itemId;
    private final String itemTitle;
    private final String borrowerId;
    private final String borrowerName;
    private final MembershipTier memberTier;
    private final int scheduledDays;
    private int actualDaysKept;
    private double baseFine;
    private double discountAmount;
    private double netFinePaid;
    private boolean returned;
    private final String borrowTimestamp;
    private String returnTimestamp;

    public LoanTransaction(String itemId, String itemTitle, String borrowerId,
                           String borrowerName, MembershipTier memberTier, int scheduledDays) {
        this.transactionId = "TXN-" + (++transactionCounter);
        this.itemId = itemId;
        this.itemTitle = itemTitle;
        this.borrowerId = borrowerId;
        this.borrowerName = borrowerName;
        this.memberTier = (memberTier == null) ? MembershipTier.REGULAR : memberTier;
        this.scheduledDays = scheduledDays;
        this.actualDaysKept = 0;
        this.baseFine = 0.0;
        this.discountAmount = 0.0;
        this.netFinePaid = 0.0;
        this.returned = false;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.borrowTimestamp = sdf.format(new Date());
        this.returnTimestamp = "PENDING";
    }

    public String getTransactionId() {
        return transactionId;
    }

    public String getItemId() {
        return itemId;
    }

    public String getItemTitle() {
        return itemTitle;
    }

    public String getBorrowerId() {
        return borrowerId;
    }

    public String getBorrowerName() {
        return borrowerName;
    }

    public MembershipTier getMemberTier() {
        return memberTier;
    }

    public int getScheduledDays() {
        return scheduledDays;
    }

    public int getActualDaysKept() {
        return actualDaysKept;
    }

    public double getBaseFine() {
        return baseFine;
    }

    public double getDiscountAmount() {
        return discountAmount;
    }

    public double getNetFinePaid() {
        return netFinePaid;
    }

    public boolean isReturned() {
        return returned;
    }

    public String getBorrowTimestamp() {
        return borrowTimestamp;
    }

    public String getReturnTimestamp() {
        return returnTimestamp;
    }

    /**
     * Marks transaction returned and computes tier-adjusted net fine.
     */
    public void recordReturn(int actualDays, double baseCalculatedFine) {
        this.actualDaysKept = actualDays;
        this.baseFine = baseCalculatedFine;
        this.netFinePaid = memberTier.calculateDiscountedFine(baseCalculatedFine);
        this.discountAmount = Math.max(0.0, this.baseFine - this.netFinePaid);
        this.returned = true;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        this.returnTimestamp = sdf.format(new Date());
    }

    /**
     * Formats an itemized ASCII receipt.
     */
    public String generateReceipt() {
        StringBuilder sb = new StringBuilder();
        int overdueDays = Math.max(0, actualDaysKept - scheduledDays);

        sb.append("========================================================\n");
        sb.append("           METROPOLIS CENTRAL PUBLIC LIBRARY            \n");
        sb.append("              OFFICIAL CIRCULATION RECEIPT              \n");
        sb.append("========================================================\n");
        sb.append(String.format(" Transaction ID : %-37s\n", transactionId));
        sb.append(String.format(" Date Issued    : %-37s\n", returnTimestamp));
        sb.append(String.format(" Patron ID      : %-37s\n", borrowerId));
        sb.append(String.format(" Patron Name    : %-37s\n", borrowerName));
        sb.append(String.format(" Membership     : %-37s\n", memberTier.getDescription()));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format(" Item Barcode   : %-37s\n", itemId));
        sb.append(String.format(" Item Title     : %-37s\n", itemTitle));
        sb.append(String.format(" Scheduled Loan : %d days\n", scheduledDays));
        sb.append(String.format(" Actual Kept    : %d days\n", actualDaysKept));
        sb.append(String.format(" Overdue Days   : %d days\n", overdueDays));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format(" Gross Overdue Fine : $%10.2f\n", baseFine));
        sb.append(String.format(" Tier Discount (%.0f%%) :-$%10.2f\n", memberTier.getFineDiscountRate() * 100, discountAmount));
        sb.append("--------------------------------------------------------\n");
        sb.append(String.format(" TOTAL AMOUNT DUE   : $%10.2f\n", netFinePaid));
        sb.append("========================================================\n");
        sb.append("               ||||| |||| |||||| |||| ||                \n");
        sb.append(String.format("                   *%s*                         \n", transactionId));
        sb.append("           Thank you for returning on time!             \n");
        sb.append("========================================================\n");

        return sb.toString();
    }
}

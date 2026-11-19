/**
 * Represents reference materials (encyclopedias, rare manuscripts, research journals)
 * which are strictly non-circulating and designated for in-library reading only.
 */
public class ReferenceMaterial extends LibraryItem {
    public static final int MAX_REFERENCE_LOAN_DAYS = 0;
    public static final double DAILY_FINE_RATE = 10.00;

    private final String subjectField;
    private final String shelfLocation;

    public ReferenceMaterial(String itemId, String title, int publicationYear,
                             String subjectField, String shelfLocation) {
        super(itemId, title, publicationYear);
        this.subjectField = (subjectField == null || subjectField.trim().isEmpty()) ? "General Reference" : subjectField.trim();
        this.shelfLocation = (shelfLocation == null || shelfLocation.trim().isEmpty()) ? "Main Reading Room" : shelfLocation.trim();
    }

    public String getSubjectField() {
        return subjectField;
    }

    public String getShelfLocation() {
        return shelfLocation;
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_REFERENCE_LOAN_DAYS;
    }

    @Override
    public double getDailyFineRate() {
        return DAILY_FINE_RATE;
    }

    @Override
    public String getItemType() {
        return "Reference Material (Non-Circulating)";
    }

    @Override
    public void borrowItem(String borrowerId, int durationDays)
            throws ItemUnavailableException, InvalidDurationException {
        // Reference materials cannot be checked out for outside removal
        throw new InvalidDurationException(durationDays,
                String.format("Reference item '%s' is strictly non-circulating. Permitted in-library reading only (0 days).",
                        getTitle()));
    }

    @Override
    public String getDetails() {
        return String.format("Type: %s | Subject: %s | Shelf: %s | Restriction: In-Library Use Only",
                getItemType(), subjectField, shelfLocation);
    }
}

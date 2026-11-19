/**
 * Enumeration representing library patron membership tiers,
 * their borrowing quotas, loan extensions, and fine discounts.
 */
public enum MembershipTier {
    REGULAR("Standard Patron", 3, 1.0, 0.0),
    STUDENT("Student Scholar", 5, 1.5, 0.25),
    FACULTY("Faculty / Researcher", 10, 2.0, 0.50);

    private final String description;
    private final int maxBorrowLimit;
    private final double maxDaysMultiplier;
    private final double fineDiscountRate;

    MembershipTier(String description, int maxBorrowLimit, double maxDaysMultiplier, double fineDiscountRate) {
        this.description = description;
        this.maxBorrowLimit = maxBorrowLimit;
        this.maxDaysMultiplier = maxDaysMultiplier;
        this.fineDiscountRate = fineDiscountRate;
    }

    public String getDescription() {
        return description;
    }

    public int getMaxBorrowLimit() {
        return maxBorrowLimit;
    }

    public double getMaxDaysMultiplier() {
        return maxDaysMultiplier;
    }

    public double getFineDiscountRate() {
        return fineDiscountRate;
    }

    /**
     * Calculates the net fine after applying the tier discount.
     * @param baseFine gross fine before discount
     * @return net fine payable
     */
    public double calculateDiscountedFine(double baseFine) {
        if (baseFine <= 0.0) {
            return 0.0;
        }
        double discount = baseFine * fineDiscountRate;
        return Math.max(0.0, baseFine - discount);
    }

    /**
     * Parses a string safely into a MembershipTier.
     * @param text input string
     * @return matched MembershipTier or REGULAR by default
     */
    public static MembershipTier fromString(String text) {
        if (text == null) {
            return REGULAR;
        }
        String clean = text.trim().toUpperCase();
        for (MembershipTier tier : values()) {
            if (tier.name().equals(clean) || tier.description.equalsIgnoreCase(clean)) {
                return tier;
            }
        }
        return REGULAR;
    }
}

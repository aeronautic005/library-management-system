import java.util.Scanner;

/**
 * Interactive Command-Line Kiosk and Automated Driver for the
 * Automated Library Catalog & Lending/Fine Billing System.
 */
public class LibraryApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final LibraryCatalog catalog = new LibraryCatalog("Metropolis Central Public Library");

    public static void main(String[] args) {
        seedSampleInventory();
        boolean running = true;

        while (running) {
            printMainMenu();
            System.out.print("Enter your choice (1-7): ");
            String input = scanner.nextLine().trim();

            switch (input) {
                case "1":
                    catalog.displayShelfOverview();
                    break;
                case "2":
                    handleSearch();
                    break;
                case "3":
                    handleCheckout();
                    break;
                case "4":
                    handleReturn();
                    break;
                case "5":
                    catalog.generateFinancialAuditReport();
                    break;
                case "6":
                    runAutomatedTestSuite();
                    break;
                case "7":
                    System.out.println("\nThank you for visiting Metropolis Central Public Library. Goodbye!\n");
                    running = false;
                    break;
                default:
                    System.out.println("\n[!] Invalid selection. Please enter a number between 1 and 7.\n");
            }
        }
    }

    private static void printMainMenu() {
        System.out.println("==================================================================");
        System.out.println("          METROPOLIS CENTRAL PUBLIC LIBRARY - KIOSK MENU          ");
        System.out.println("==================================================================");
        System.out.println(" [1] View Catalog Shelf Map & Inventory Status");
        System.out.println(" [2] Search Catalog by Title or Barcode ID");
        System.out.println(" [3] Checkout Item (Borrow with Membership Tier)");
        System.out.println(" [4] Return Item & Process Overdue Fine Receipt");
        System.out.println(" [5] Circulation History & Financial Audit Report");
        System.out.println(" [6] Run Automated OOP Verification Test Suite");
        System.out.println(" [7] Exit Kiosk");
        System.out.println("==================================================================");
    }

    private static void seedSampleInventory() {
        // Books
        catalog.addItem(new Book("BK-101", "Clean Code", 2008,
                "Robert C. Martin", "Software Engineering", "978-0132350884"));
        catalog.addItem(new Book("BK-102", "The Pragmatic Programmer", 1999,
                "Andrew Hunt & David Thomas", "Computer Science", "978-0201616224"));
        catalog.addItem(new Book("BK-103", "Introduction to Algorithms", 2009,
                "Thomas H. Cormen", "Algorithms", "978-0262033848"));
        catalog.addItem(new Book("BK-104", "Design Patterns", 1994,
                "Gang of Four", "Software Engineering", "978-0201633610"));

        // Reference Materials (In-Library Reading Only)
        catalog.addItem(new ReferenceMaterial("REF-201", "Encyclopaedia Britannica Vol. 1", 2015,
                "World History & Geography", "Aisle 3, Shelf A"));
        catalog.addItem(new ReferenceMaterial("REF-202", "Oxford English Dictionary", 2020,
                "Linguistics & Lexicography", "Aisle 1, Shelf R"));

        // Digital Media
        catalog.addItem(new DigitalMedia("DIG-301", "Operating Systems Video Lecture Series", 2023,
                "Video Archive", 4200.0, 15.00));
        catalog.addItem(new DigitalMedia("DIG-302", "AI Modern Approach Audiobook", 2024,
                "Audiobook", 650.0, 8.50));
    }

    private static void handleSearch() {
        System.out.println("\n----------------- SEARCH CATALOG -----------------");
        System.out.print("Enter search term (Barcode ID or Title keyword): ");
        String query = scanner.nextLine().trim();

        if (query.isEmpty()) {
            System.out.println("[!] Search query cannot be blank.");
            return;
        }

        // Try exact ID match first
        LibraryItem item = catalog.findItemById(query);
        if (item != null) {
            System.out.println("\n[+] Direct Barcode Match Found:");
            System.out.println("    " + item);
            System.out.println("    " + item.getDetails());
            return;
        }

        // Otherwise search by title substring
        java.util.List<LibraryItem> matches = catalog.searchByTitle(query);
        if (matches.isEmpty()) {
            System.out.println("[-] No items found matching '" + query + "'.");
        } else {
            System.out.printf("\n[+] Found %d matching item(s):\n", matches.size());
            for (LibraryItem match : matches) {
                System.out.println(" -> " + match);
                System.out.println("    " + match.getDetails());
            }
        }
        System.out.println("--------------------------------------------------\n");
    }

    private static void handleCheckout() {
        System.out.println("\n---------------- ITEM CHECKOUT KIOSK -------------");
        System.out.print("Enter Item Barcode ID (e.g., BK-101): ");
        String itemId = scanner.nextLine().trim();

        LibraryItem item = catalog.findItemById(itemId);
        if (item == null) {
            System.out.println("[!] Error: No item cataloged with ID '" + itemId + "'.\n");
            return;
        }

        System.out.println("Selected: " + item.getTitle() + " (" + item.getItemType() + ")");
        System.out.print("Enter Patron ID (e.g., P-1001): ");
        String patronId = scanner.nextLine().trim();

        System.out.print("Enter Patron Full Name: ");
        String patronName = scanner.nextLine().trim();

        System.out.println("Select Membership Tier:");
        System.out.println(" 1. REGULAR (Limit: 3 items, 1.0x days, 0% fine discount)");
        System.out.println(" 2. STUDENT (Limit: 5 items, 1.5x days, 25% fine discount)");
        System.out.println(" 3. FACULTY (Limit: 10 items, 2.0x days, 50% fine discount)");
        System.out.print("Choice (1-3, default 1): ");
        String tierChoice = scanner.nextLine().trim();

        MembershipTier tier;
        if ("2".equals(tierChoice)) {
            tier = MembershipTier.STUDENT;
        } else if ("3".equals(tierChoice)) {
            tier = MembershipTier.FACULTY;
        } else {
            tier = MembershipTier.REGULAR;
        }

        int maxAllowedDays = (int) Math.round(item.getMaxLoanDays() * tier.getMaxDaysMultiplier());
        System.out.printf("Maximum loan period for this item under %s: %d days\n",
                tier.getDescription(), maxAllowedDays);

        System.out.print("Enter requested loan duration in days: ");
        String daysInput = scanner.nextLine().trim();

        int durationDays;
        try {
            durationDays = Integer.parseInt(daysInput);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid input. Loan duration must be an integer.\n");
            return;
        }

        try {
            LoanTransaction tx = catalog.checkoutItem(itemId, patronId, patronName, tier, durationDays);
            System.out.println("\n================ CHECKOUT SUCCESSFUL ================");
            System.out.println(" Transaction ID : " + tx.getTransactionId());
            System.out.println(" Item Borrowed  : " + tx.getItemTitle() + " [" + tx.getItemId() + "]");
            System.out.println(" Borrower       : " + tx.getBorrowerName() + " (" + tx.getBorrowerId() + ")");
            System.out.println(" Membership     : " + tx.getMemberTier().getDescription());
            System.out.println(" Loan Period    : " + tx.getScheduledDays() + " day(s)");
            System.out.println(" Timestamp      : " + tx.getBorrowTimestamp());
            System.out.println(" Please return the item on or before the due period to avoid fines.");
            System.out.println("=====================================================\n");
        } catch (ItemUnavailableException e) {
            System.out.println("\n[!] CHECKOUT REJECTED: " + e.getMessage());
            System.out.println("    Item is already held by patron: " + e.getCurrentBorrowerId() + "\n");
        } catch (BorrowLimitExceededException e) {
            System.out.println("\n[!] CHECKOUT REJECTED: " + e.getMessage());
            System.out.println("    Patron currently has reached their maximum quota of " + e.getMaxAllowed() + " items.\n");
        } catch (InvalidDurationException e) {
            System.out.println("\n[!] DURATION ERROR: " + e.getMessage());
            System.out.println("    Please select a valid duration within the allowable parameters.\n");
        } catch (Exception e) {
            System.out.println("\n[!] Unexpected Error: " + e.getMessage() + "\n");
        }
    }

    private static void handleReturn() {
        System.out.println("\n---------------- ITEM RETURN & BILLING -----------");
        System.out.print("Enter Item Barcode ID to return (e.g., BK-101): ");
        String itemId = scanner.nextLine().trim();

        System.out.print("Enter actual days kept in possession: ");
        String daysInput = scanner.nextLine().trim();

        int actualDays;
        try {
            actualDays = Integer.parseInt(daysInput);
        } catch (NumberFormatException e) {
            System.out.println("[!] Invalid input. Actual days kept must be an integer.\n");
            return;
        }

        try {
            LoanTransaction tx = catalog.returnItem(itemId, actualDays);
            System.out.println("\n[+] Item returned successfully! Generating official receipt...\n");
            System.out.println(tx.generateReceipt());
        } catch (InvalidDurationException e) {
            System.out.println("\n[!] INVALID DURATION: " + e.getMessage() + "\n");
        } catch (IllegalStateException e) {
            System.out.println("\n[!] ERROR: " + e.getMessage() + "\n");
        } catch (IllegalArgumentException e) {
            System.out.println("\n[!] ERROR: " + e.getMessage() + "\n");
        } catch (Exception e) {
            System.out.println("\n[!] Unexpected Error: " + e.getMessage() + "\n");
        }
    }

    private static void runAutomatedTestSuite() {
        System.out.println("\n==================================================================");
        System.out.println("     RUNNING AUTOMATED OOP VERIFICATION & EXCEPTION TEST SUITE     ");
        System.out.println("==================================================================");

        int passed = 0;
        int total = 0;

        // Test 1: On-time Book Return
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            Book b1 = new Book("T-BK1", "Test Book 1", 2021, "Author A", "Tech", "12345");
            testCatalog.addItem(b1);
            testCatalog.checkoutItem("T-BK1", "U1", "Alice", MembershipTier.REGULAR, 7);
            LoanTransaction tx = testCatalog.returnItem("T-BK1", 5); // Returned within 7 days
            if (tx.getNetFinePaid() == 0.0 && b1.isAvailable()) {
                System.out.println(" [PASS] Test 1: On-time Book return calculates $0.00 fine and restores availability.");
                passed++;
            } else {
                System.out.println(" [FAIL] Test 1: Fine mismatch or availability not restored.");
            }
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 1 encountered exception: " + e.getMessage());
        }

        // Test 2: Overdue Book Fine Calculation (Standard Patron)
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            Book b2 = new Book("T-BK2", "Test Book 2", 2020, "Author B", "Science", "67890");
            testCatalog.addItem(b2);
            testCatalog.checkoutItem("T-BK2", "U2", "Bob", MembershipTier.REGULAR, 10);
            LoanTransaction tx = testCatalog.returnItem("T-BK2", 14); // 4 days overdue * $1.00 = $4.00
            if (Math.abs(tx.getNetFinePaid() - 4.00) < 0.001) {
                System.out.println(" [PASS] Test 2: Regular Patron overdue fine calculated accurately ($4.00 for 4 days).");
                passed++;
            } else {
                System.out.printf(" [FAIL] Test 2: Expected $4.00, got $%.2f\n", tx.getNetFinePaid());
            }
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 2 encountered exception: " + e.getMessage());
        }

        // Test 3: Student Tier 25% Fine Discount
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            Book b3 = new Book("T-BK3", "Test Book 3", 2020, "Author C", "History", "11223");
            testCatalog.addItem(b3);
            testCatalog.checkoutItem("T-BK3", "S1", "Charlie", MembershipTier.STUDENT, 10);
            LoanTransaction tx = testCatalog.returnItem("T-BK3", 14); // 4 days overdue: gross $4.00, net $3.00 (25% off)
            if (Math.abs(tx.getNetFinePaid() - 3.00) < 0.001 && Math.abs(tx.getDiscountAmount() - 1.00) < 0.001) {
                System.out.println(" [PASS] Test 3: Student Scholar receives 25% fine discount ($3.00 net on $4.00 gross).");
                passed++;
            } else {
                System.out.printf(" [FAIL] Test 3: Expected $3.00 net, got $%.2f\n", tx.getNetFinePaid());
            }
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 3 encountered exception: " + e.getMessage());
        }

        // Test 4: Faculty Tier 50% Fine Discount
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            Book b4 = new Book("T-BK4", "Test Book 4", 2019, "Author D", "Math", "33445");
            testCatalog.addItem(b4);
            testCatalog.checkoutItem("T-BK4", "F1", "Dr. Stone", MembershipTier.FACULTY, 10);
            LoanTransaction tx = testCatalog.returnItem("T-BK4", 14); // 4 days overdue: gross $4.00, net $2.00 (50% off)
            if (Math.abs(tx.getNetFinePaid() - 2.00) < 0.001 && Math.abs(tx.getDiscountAmount() - 2.00) < 0.001) {
                System.out.println(" [PASS] Test 4: Faculty Member receives 50% fine discount ($2.00 net on $4.00 gross).");
                passed++;
            } else {
                System.out.printf(" [FAIL] Test 4: Expected $2.00 net, got $%.2f\n", tx.getNetFinePaid());
            }
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 4 encountered exception: " + e.getMessage());
        }

        // Test 5: ItemUnavailableException Handling
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            Book b5 = new Book("T-BK5", "Test Book 5", 2022, "Author E", "Design", "55667");
            testCatalog.addItem(b5);
            testCatalog.checkoutItem("T-BK5", "U1", "Alice", MembershipTier.REGULAR, 7);
            // Attempt to checkout again by another user
            testCatalog.checkoutItem("T-BK5", "U2", "Bob", MembershipTier.REGULAR, 7);
            System.out.println(" [FAIL] Test 5: Failed to throw ItemUnavailableException on already checked out item.");
        } catch (ItemUnavailableException e) {
            System.out.println(" [PASS] Test 5: Successfully intercepted ItemUnavailableException: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 5: Threw unexpected exception type: " + e.getClass().getName());
        }

        // Test 6: BorrowLimitExceededException Handling
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            for (int i = 1; i <= 4; i++) {
                testCatalog.addItem(new Book("T-BK" + (10 + i), "Title " + i, 2020, "Author", "Gen", "ISBN" + i));
            }
            // Regular tier limit is 3 items
            testCatalog.checkoutItem("T-BK11", "U1", "Alice", MembershipTier.REGULAR, 7);
            testCatalog.checkoutItem("T-BK12", "U1", "Alice", MembershipTier.REGULAR, 7);
            testCatalog.checkoutItem("T-BK13", "U1", "Alice", MembershipTier.REGULAR, 7);
            // 4th checkout should throw BorrowLimitExceededException
            testCatalog.checkoutItem("T-BK14", "U1", "Alice", MembershipTier.REGULAR, 7);
            System.out.println(" [FAIL] Test 6: Failed to enforce borrow quota limit.");
        } catch (BorrowLimitExceededException e) {
            System.out.println(" [PASS] Test 6: Successfully intercepted BorrowLimitExceededException (Quota = " + e.getMaxAllowed() + ").");
            passed++;
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 6: Threw unexpected exception type: " + e.getClass().getName());
        }

        // Test 7: Non-Circulating Reference Material Restriction
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            ReferenceMaterial ref = new ReferenceMaterial("T-REF1", "Rare Atlas", 1910, "Geography", "Vault");
            testCatalog.addItem(ref);
            testCatalog.checkoutItem("T-REF1", "U1", "Alice", MembershipTier.REGULAR, 3);
            System.out.println(" [FAIL] Test 7: Allowed checkout of non-circulating reference material.");
        } catch (InvalidDurationException e) {
            System.out.println(" [PASS] Test 7: Successfully blocked reference loan with InvalidDurationException: " + e.getMessage());
            passed++;
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 7: Threw unexpected exception type: " + e.getClass().getName());
        }

        // Test 8: Non-Positive Duration Validation
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            Book b8 = new Book("T-BK8", "Test Book 8", 2021, "Author F", "Fiction", "99887");
            testCatalog.addItem(b8);
            testCatalog.checkoutItem("T-BK8", "U1", "Alice", MembershipTier.REGULAR, 0);
            System.out.println(" [FAIL] Test 8: Allowed non-positive duration (0 days).");
        } catch (InvalidDurationException e) {
            System.out.println(" [PASS] Test 8: Successfully rejected non-positive duration with InvalidDurationException.");
            passed++;
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 8: Threw unexpected exception type: " + e.getClass().getName());
        }

        // Test 9: Digital Media Overdue Fine & License Fee
        total++;
        try {
            LibraryCatalog testCatalog = new LibraryCatalog("Test Facility");
            DigitalMedia dig = new DigitalMedia("T-DIG1", "Cloud Computing Video", 2023, "Video", 1200.0, 10.00);
            testCatalog.addItem(dig);
            testCatalog.checkoutItem("T-DIG1", "U1", "Alice", MembershipTier.REGULAR, 7);
            // 2 days overdue (9 days kept): base fine = (2 * $2.50) + ($10.00 * 0.50) = $5.00 + $5.00 = $10.00
            LoanTransaction tx = testCatalog.returnItem("T-DIG1", 9);
            if (Math.abs(tx.getNetFinePaid() - 10.00) < 0.001) {
                System.out.println(" [PASS] Test 9: Digital Media fine includes daily rate plus license fee ($10.00 total).");
                passed++;
            } else {
                System.out.printf(" [FAIL] Test 9: Expected $10.00, got $%.2f\n", tx.getNetFinePaid());
            }
        } catch (Exception e) {
            System.out.println(" [FAIL] Test 9 encountered exception: " + e.getMessage());
        }

        System.out.println("------------------------------------------------------------------");
        System.out.printf(" Automated Test Results: %d / %d PASSED (%.1f%%)\n",
                passed, total, ((double) passed / total) * 100);
        System.out.println("==================================================================\n");
    }
}

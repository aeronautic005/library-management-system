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
        System.out.println("\n[Automated Test Suite will be wired in the test commit]");
    }
}

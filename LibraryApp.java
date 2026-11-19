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
        System.out.println("\n[Search feature will be wired in the next commit]");
    }

    private static void handleCheckout() {
        System.out.println("\n[Checkout feature will be wired in the next commit]");
    }

    private static void handleReturn() {
        System.out.println("\n[Return feature will be wired in the next commit]");
    }

    private static void runAutomatedTestSuite() {
        System.out.println("\n[Automated Test Suite will be wired in the test commit]");
    }
}

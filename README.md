# Automated Library Catalog & Lending/Fine Billing System

An enterprise-ready Object-Oriented Java application simulating an automated modern library lending catalog, inventory shelf tracking, and overdue fine billing kiosk. Features include polymorphic media types, custom domain exceptions, membership tier benefits, itemized ASCII receipts, and an automated verification test suite.

## System Architecture

```text
+-------------------------------------------------------------------------------+
|                                  LibraryApp                                   |
|                         (CLI Kiosk & Interactive UI)                          |
+---------------------------------------+---------------------------------------+
                                        |
                                        v
+-------------------------------------------------------------------------------+
|                                LibraryCatalog                                 |
|            (Catalog Inventory, Shelf Matrix, Financial Audit Ledger)          |
+-------------------+-------------------+-------------------+-------------------+
                    |                   |                   |
                    v                   v                   v
         +--------------------+  +--------------+  +--------------------+
         |   LibraryItem      |  | LoanTransact |  |   MembershipTier   |
         |  (Abstract Base)   |  | (Receipt/Tax)|  | (Quotas & Discounts|
         +---------+----------+  +--------------+  +--------------------+
                   |
     +-------------+-------------+
     |             |             |
     v             v             v
+----------+ +-----------+ +------------+
|   Book   | | Reference | |   Digital  |
|  (Print) | | (In-Lib)  | |   (Media)  |
+----------+ +-----------+ +------------+
                   |
                   v
+-------------------------------------------------------------------------------+
|                               Exceptions Hierarchy                            |
|  + InvalidDurationException (Enforces allowable borrow windows)               |
|  + ItemUnavailableException (Prevents double checkout of on-loan media)       |
|  + BorrowLimitExceededException (Enforces member tier quota limits)           |
+-------------------------------------------------------------------------------+
```

## Lending Policy & Rate Sheet

| Media Category | Standard Loan Window | Daily Overdue Fine Rate | Licensing / Surcharge | Circulation Policy |
| :--- | :---: | :---: | :---: | :--- |
| **Printed Book** | 14 Days | $1.00 / day | None | Circulating |
| **Reference Material** | 0 Days (In-Library) | $10.00 / day | Reading Room Only | Non-Circulating |
| **Digital Media (E-Book/Audio)** | 7 Days | $2.50 / day | +50% base license fee | Circulating |

## Membership Tier Benefits

| Tier Level | Borrow Quota | Max Duration Multiplier | Overdue Fine Discount |
| :--- | :---: | :---: | :---: |
| **REGULAR (Standard Patron)** | 3 items | 1.0x (Standard) | 0% |
| **STUDENT (Student Scholar)** | 5 items | 1.5x (Extended) | 25% OFF |
| **FACULTY (Faculty / Researcher)**| 10 items | 2.0x (Double Duration) | 50% OFF |

## Key Object-Oriented Principles Implemented

1. **Encapsulation & Validation:** All entity fields are protected/private with domain boundary checks.
2. **Polymorphism & Abstraction:** Abstract `LibraryItem` implements the `Borrowable` interface, with specialized behavior in `Book`, `ReferenceMaterial`, and `DigitalMedia`.
3. **Custom Exceptions:** Robust error handling preventing checkout collisions (`ItemUnavailableException`), quota overflow (`BorrowLimitExceededException`), and duration violations (`InvalidDurationException`).
4. **Enums with Business Logic:** `MembershipTier` encapsulates discount formulas, quota rules, and string parsing.
5. **Interactive Kiosk & Test Suite:** Interactive console UI with ASCII visual shelf maps, financial audit reports, and a built-in automated test suite covering 9 unit test scenarios.

## How to Compile & Run

```bash
# Navigate to project directory
cd library-management-system

# Compile all Java source files
javac *.java

# Run the interactive Library Kiosk
java LibraryApp
```

### Running the Built-in Test Suite
Launch `LibraryApp` and select option `[6]` from the main menu to run the automated unit and exception verification test suite.

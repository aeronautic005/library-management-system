/**
 * Represents a standard printed circulating book in the library collection.
 */
public class Book extends LibraryItem {
    public static final int MAX_BOOK_LOAN_DAYS = 14;
    public static final double DAILY_FINE_RATE = 1.00;

    private final String author;
    private final String genre;
    private final String isbn;

    public Book(String itemId, String title, int publicationYear, String author, String genre, String isbn) {
        super(itemId, title, publicationYear);
        this.author = (author == null || author.trim().isEmpty()) ? "Unknown Author" : author.trim();
        this.genre = (genre == null || genre.trim().isEmpty()) ? "General" : genre.trim();
        this.isbn = (isbn == null || isbn.trim().isEmpty()) ? "N/A" : isbn.trim();
    }

    public String getAuthor() {
        return author;
    }

    public String getGenre() {
        return genre;
    }

    public String getIsbn() {
        return isbn;
    }

    @Override
    public int getMaxLoanDays() {
        return MAX_BOOK_LOAN_DAYS;
    }

    @Override
    public double getDailyFineRate() {
        return DAILY_FINE_RATE;
    }

    @Override
    public String getItemType() {
        return "Printed Book";
    }

    @Override
    public String getDetails() {
        return String.format("Type: %s | ISBN: %s | Author: %s | Genre: %s | Max Loan: %d days | Fine: $%.2f/day",
                getItemType(), isbn, author, genre, getMaxLoanDays(), getDailyFineRate());
    }
}

package library;

public class PrintedBook extends LibraryItem {
    private String isbn;
    private int pageCount;

    public PrintedBook(int id, String title, String author, int publicationYear, int availableCopies, String isbn, int pageCount) {
        super(id, title, author, publicationYear, availableCopies);
        this.isbn = isbn;
        this.pageCount = pageCount;
    }

    public String getIsbn() {
        return isbn;
    }

    public int getPageCount() {
        return pageCount;
    }

    @Override
    public void printInfo() {
        System.out.println("--- PrintedBook ---");
        printBaseInfo();
        System.out.println("ISBN: " + isbn);
        System.out.println("Pages: " + pageCount);
    }

    @Override
    public boolean validateData() {
        return getTitle() != null && !getTitle().isBlank()
                && getAuthor() != null && !getAuthor().isBlank()
                && getPublicationYear() > 0
                && getAvailableCopies() >= 0 && getAvailableCopies() <= MAX_COPIES
                && isbn != null && !isbn.isBlank()
                && pageCount > 0;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * 1.50;
    }
}

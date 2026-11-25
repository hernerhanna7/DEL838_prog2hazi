package library;

public class ItemFactory {
    private static int nextId = 1;

    private ItemFactory() {}

    public static LibraryItem createPrintedBook(
            String title,
            String author,
            int publicationYear,
            int availableCopies,
            String isbn,
            int pageCount
    ) {
        PrintedBook book = new PrintedBook(
                nextId, title, author, publicationYear, availableCopies, isbn, pageCount
        );
        if (!book.validateData()) return null;

        nextId++;
        return book;
    }

    public static LibraryItem createEBook(
            String title,
            String author,
            int publicationYear,
            int availableCopies,
            String format,
            double fileSizeMb
    ) {
        EBook ebook = new EBook(
                nextId, title, author, publicationYear, availableCopies, format, fileSizeMb
        );
        if (!ebook.validateData()) return null;

        nextId++;
        return ebook;
    }

    public static void syncNextId(int maxExistingId) {
        if (maxExistingId >= nextId) {
            nextId = maxExistingId + 1;
        }
    }
}

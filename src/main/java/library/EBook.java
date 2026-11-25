package library;

public class EBook extends LibraryItem {
    private String format;
    private double fileSizeMb;

    public EBook(int id, String title, String author, int publicationYear, int availableCopies, String format, double fileSizeMb) {
        super(id, title, author, publicationYear, availableCopies);
        this.format = format;
        this.fileSizeMb = fileSizeMb;
    }

    public String getFormat() {
        return format;
    }

    public double getFileSizeMb() {
        return fileSizeMb;
    }

    @Override
    public void printInfo() {
        System.out.println("--- EBook ---");
        printBaseInfo();
        System.out.println("Format: " + format);
        System.out.println("File Size (MB): " + fileSizeMb);
    }

    @Override
    public boolean validateData() {
        return getTitle() != null && !getTitle().isBlank()
                && getAuthor() != null && !getAuthor().isBlank()
                && getPublicationYear() > 0
                && getAvailableCopies() >= 0 && getAvailableCopies() <= MAX_COPIES
                && format != null && !format.isBlank()
                && fileSizeMb > 0;
    }

    @Override
    public double calculateLateFee(int daysLate) {
        if (daysLate <= 0) return 0.0;
        return daysLate * 0.50;
    }
}

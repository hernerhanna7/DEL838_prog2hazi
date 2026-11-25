package library;

public abstract class LibraryItem {
    public static final int MAX_COPIES = 100;

    private int id;
    private String title;
    private String author;
    private int publicationYear;
    private int availableCopies;

    public LibraryItem(int id, String title, String author, int publicationYear, int availableCopies) {
        this.id = id;
        this.title = title;
        this.author = author;
        this.publicationYear = publicationYear;
        this.availableCopies = availableCopies;
    }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getAuthor() {
        return author;
    }

    public int getPublicationYear() {
        return publicationYear;
    }

    public int getAvailableCopies() {
        return availableCopies;
    }

    public boolean addCopies(int count) {
        if (count <= 0) return false;
        if (this.availableCopies + count > MAX_COPIES) return false;
        this.availableCopies += count;
        return true;
    }

    public boolean borrowCopy() {
        if (this.availableCopies <= 0) return false;
        this.availableCopies--;
        return true;
    }

    public boolean returnCopy() {
        if (this.availableCopies + 1 > MAX_COPIES) return false;
        this.availableCopies++;
        return true;
    }

    public abstract void printInfo();

    public abstract boolean validateData();

    public abstract double calculateLateFee(int daysLate);

    protected void printBaseInfo() {
        System.out.println("ID: " + id);
        System.out.println("Title: " + title);
        System.out.println("Author: " + author);
        System.out.println("Year: " + publicationYear);
        System.out.println("Available Copies: " + availableCopies);
    }
}

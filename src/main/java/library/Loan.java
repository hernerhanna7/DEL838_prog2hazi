package library;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan {
    private int loanId;
    private Reader reader;
    private LibraryItem item;
    private LocalDate borrowDate;
    private LocalDate dueDate;
    private boolean returned;

    public Loan(int loanId, Reader reader, LibraryItem item, LocalDate borrowDate, LocalDate dueDate) {
        this.loanId = loanId;
        this.reader = reader;
        this.item = item;
        this.borrowDate = borrowDate;
        this.dueDate = dueDate;
        this.returned = false;
    }

    public int getLoanId() {
        return loanId;
    }

    public Reader getReader() {
        return reader;
    }

    public LibraryItem getItem() {
        return item;
    }

    public LocalDate getBorrowDate() {
        return borrowDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public boolean isReturned() {
        return returned;
    }

    public void setReturned(boolean returned) {
        this.returned = returned;
    }

    public double returnItem(LocalDate returnDate) {
        this.returned = true;
        long daysLate = ChronoUnit.DAYS.between(dueDate, returnDate);
        return item.calculateLateFee((int) Math.max(0, daysLate));
    }

    public void printLoanInfo() {
        System.out.println("Loan ID: " + loanId
                + " | Item: " + item.getTitle() + " (ID: " + item.getId() + ")"
                + " | Reader: " + reader.getName()
                + " | Borrowed: " + borrowDate
                + " | Due: " + dueDate
                + " | Returned: " + returned);
    }
}

package library;

import java.util.ArrayList;
import java.util.List;

public class Reader extends User {
    public static final int MAX_ACTIVE_LOANS = 3;
    private List<Loan> activeLoans;

    public Reader(int id, String name, String email) {
        super(id, name, email);
        this.activeLoans = new ArrayList<>();
    }

    public List<Loan> getActiveLoans() {
        return activeLoans;
    }

    public boolean canBorrowMore() {
        return activeLoans.size() < MAX_ACTIVE_LOANS;
    }

    public void addLoan(Loan loan) {
        activeLoans.add(loan);
    }

    public void removeLoan(Loan loan) {
        activeLoans.remove(loan);
    }

    @Override
    public boolean canManageItems() {
        return false;
    }

    @Override
    public String getRoleName() {
        return "READER";
    }
}

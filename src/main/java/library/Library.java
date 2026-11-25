package library;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Library {
    public static final int UNLIMITED_CAPACITY = Integer.MAX_VALUE;

    private List<LibraryItem> items;
    private Map<Integer, LibraryItem> itemById;
    private List<User> users;
    private Map<Integer, User> userById;
    private List<Loan> loans;
    private int capacity;
    private int nextLoanId = 1;
    private int nextUserId = 1;
    private SearchStrategy searchStrategy;
    private LibraryStorage storage;

    public Library() {
        this(UNLIMITED_CAPACITY);
    }

    public Library(int capacity) {
        this.capacity = capacity > 0 ? capacity : UNLIMITED_CAPACITY;
        this.searchStrategy = new TitleSearchStrategy();
        this.storage = new LibraryStorage();

        this.items = storage.loadItems();
        this.itemById = new HashMap<>(Math.max(16, this.items.size() * 2));
        for (LibraryItem item : this.items) {
            this.itemById.put(item.getId(), item);
        }

        this.users = storage.loadUsers();
        this.userById = new HashMap<>(Math.max(16, this.users.size() * 2));
        int maxUserId = 0;
        for (User user : this.users) {
            this.userById.put(user.getId(), user);
            if (user.getId() > maxUserId) {
                maxUserId = user.getId();
            }
        }
        this.nextUserId = maxUserId + 1;

        LibraryStorage.LoadedLoansData loadedLoans = storage.loadActiveLoans(this.itemById, this.userById);
        this.loans = new ArrayList<>(loadedLoans.getActiveLoans());
        this.nextLoanId = loadedLoans.getMaxLoanId() + 1;
    }

    public void setSearchStrategy(SearchStrategy searchStrategy) {
        this.searchStrategy = searchStrategy;
    }

    public void registerUser(User user) {
        if (user != null && !userById.containsKey(user.getId())) {
            users.add(user);
            userById.put(user.getId(), user);
            if (user.getId() >= nextUserId) {
                nextUserId = user.getId() + 1;
            }
            storage.saveUsers(users);
        }
    }

    public Reader registerNewReader(String name, String email) throws LibraryException {
        if (name == null || name.isBlank()) {
            throw new LibraryException("A nev nem lehet ures!");
        }
        if (email == null || email.isBlank() || !email.contains("@")) {
            throw new LibraryException("Ervenytelen email cim!");
        }
        Reader reader = new Reader(nextUserId++, name.trim(), email.trim());
        users.add(reader);
        userById.put(reader.getId(), reader);
        storage.saveUsers(users);
        return reader;
    }

    public User findUserById(int id) {
        return userById.get(id);
    }

    public List<User> getUsers() {
        return users;
    }

    public LibraryItem findItemById(int id) {
        return itemById.get(id);
    }

    public boolean addItem(User actor, LibraryItem item) throws UnauthorizedActionException {
        if (actor == null || !actor.canManageItems()) {
            throw new UnauthorizedActionException("Csak Manager adhat hozza uj konyvet!");
        }
        if (item == null || items.size() >= capacity) return false;
        if (itemById.containsKey(item.getId())) return false;

        items.add(item);
        itemById.put(item.getId(), item);
        storage.saveItems(items);
        return true;
    }

    public boolean addCopies(User actor, int itemId, int count) throws UnauthorizedActionException {
        if (actor == null || !actor.canManageItems()) {
            throw new UnauthorizedActionException("Csak Manager modosithat peldanyszamot!");
        }
        LibraryItem item = findItemById(itemId);
        if (item == null) return false;

        boolean added = item.addCopies(count);
        if (added) {
            storage.saveItems(items);
        }
        return added;
    }

    public boolean removeItem(User actor, int itemId) throws UnauthorizedActionException {
        if (actor == null || !actor.canManageItems()) {
            throw new UnauthorizedActionException("Csak Manager torolhet konyvet!");
        }
        LibraryItem item = findItemById(itemId);
        if (item == null) return false;

        for (Loan loan : loans) {
            if (!loan.isReturned() && loan.getItem().getId() == itemId) {
                return false;
            }
        }
        boolean removed = items.remove(item);
        if (removed) {
            itemById.remove(itemId);
            storage.saveItems(items);
        }
        return removed;
    }

    public List<LibraryItem> searchItems(String query) {
        return searchStrategy.search(items, query);
    }

    public List<LibraryItem> getAllItemsSortedByTitle() {
        List<LibraryItem> sorted = new ArrayList<>(items);
        sorted.sort(Comparator.comparing(LibraryItem::getTitle, String.CASE_INSENSITIVE_ORDER));
        return sorted;
    }

    public Loan borrowItem(User actor, int itemId) throws LibraryException {
        if (!(actor instanceof Reader reader)) {
            throw new UnauthorizedActionException("Csak Reader (olvaso) kolcsonozhet konyvet!");
        }
        if (!reader.canBorrowMore()) {
            throw new ItemNotAvailableException("Elerted a maximalis kolcsonzesi limitet (" + Reader.MAX_ACTIVE_LOANS + " db)!");
        }
        LibraryItem item = findItemById(itemId);
        if (item == null) {
            throw new ItemNotAvailableException("Nem talalhato ilyen ID-ju konyv!");
        }
        if (!item.borrowCopy()) {
            throw new ItemNotAvailableException("Nincs elerheto peldany ebbol a konyvbol!");
        }

        Loan loan = new Loan(nextLoanId++, reader, item, LocalDate.now(), LocalDate.now().plusDays(14));
        loans.add(loan);
        reader.addLoan(loan);
        storage.saveItems(items);
        storage.saveActiveLoans(loans);
        return loan;
    }

    public double returnItem(User actor, int loanId) throws LibraryException {
        if (!(actor instanceof Reader reader)) {
            throw new UnauthorizedActionException("Csak Reader adhat vissza konyvet!");
        }
        for (Loan loan : reader.getActiveLoans()) {
            if (loan.getLoanId() == loanId && !loan.isReturned()) {
                double fee = loan.returnItem(LocalDate.now());
                loan.getItem().returnCopy();
                reader.removeLoan(loan);
                loans.remove(loan);
                storage.saveItems(items);
                storage.saveActiveLoans(loans);
                return fee;
            }
        }
        throw new ItemNotAvailableException("Nem talalhato ilyen aktiv kolcsonzes ennel az olvasonal!");
    }

    public List<Loan> getAllLoans() {
        return loans;
    }
}

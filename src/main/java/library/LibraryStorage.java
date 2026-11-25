package library;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LibraryStorage {
    private static final String ITEMS_FILE_NAME = "library_items.csv";
    private static final String USERS_FILE_NAME = "library_users.txt";
    private static final String LOANS_FILE_NAME = "library_loans.txt";
    private static final int IO_BUFFER_SIZE = 65536;

    public void saveItems(List<LibraryItem> items) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ITEMS_FILE_NAME), StandardCharsets.UTF_8),
                IO_BUFFER_SIZE);
             PrintWriter writer = new PrintWriter(bw)) {
            for (LibraryItem item : items) {
                if (item instanceof PrintedBook pb) {
                    writer.println("PRINTED;" + pb.getId() + ";" + sanitize(pb.getTitle()) + ";"
                            + sanitize(pb.getAuthor()) + ";" + pb.getPublicationYear() + ";"
                            + pb.getAvailableCopies() + ";" + sanitize(pb.getIsbn()) + ";"
                            + pb.getPageCount());
                } else if (item instanceof EBook eb) {
                    writer.println("EBOOK;" + eb.getId() + ";" + sanitize(eb.getTitle()) + ";"
                            + sanitize(eb.getAuthor()) + ";" + eb.getPublicationYear() + ";"
                            + eb.getAvailableCopies() + ";" + sanitize(eb.getFormat()) + ";"
                            + eb.getFileSizeMb());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving library items: " + e.getMessage());
        }
    }

    public List<LibraryItem> loadItems() {
        List<LibraryItem> items = new ArrayList<>();
        File file = new File(ITEMS_FILE_NAME);
        if (!file.exists()) {
            return items;
        }

        int maxId = 0;
        long lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
                IO_BUFFER_SIZE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 8) {
                    System.out.println("Skipping malformed item line " + lineNumber);
                    continue;
                }

                try {
                    String type = parts[0].trim();
                    int id = Integer.parseInt(parts[1].trim());
                    String title = parts[2];
                    String author = parts[3];
                    int year = Integer.parseInt(parts[4].trim());
                    int copies = Integer.parseInt(parts[5].trim());

                    if (id > maxId) maxId = id;

                    if ("PRINTED".equals(type)) {
                        String isbn = parts[6];
                        int pages = Integer.parseInt(parts[7].trim());
                        items.add(new PrintedBook(id, title, author, year, copies, isbn, pages));
                    } else if ("EBOOK".equals(type)) {
                        String format = parts[6];
                        double size = Double.parseDouble(parts[7].trim());
                        items.add(new EBook(id, title, author, year, copies, format, size));
                    } else {
                        System.out.println("Unknown item type on line " + lineNumber + ": " + type);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number on item line " + lineNumber + ": " + e.getMessage());
                }
            }
            ItemFactory.syncNextId(maxId);
        } catch (IOException e) {
            System.out.println("Error loading library items: " + e.getMessage());
        }
        return items;
    }

    public void saveUsers(List<User> users) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(USERS_FILE_NAME), StandardCharsets.UTF_8),
                IO_BUFFER_SIZE);
             PrintWriter writer = new PrintWriter(bw)) {
            for (User user : users) {
                if (user instanceof Manager manager) {
                    writer.println("MANAGER;" + manager.getId() + ";" + sanitize(manager.getName()) + ";"
                            + sanitize(manager.getEmail()) + ";" + sanitize(manager.getEmployeeCode()));
                } else if (user instanceof Reader reader) {
                    writer.println("READER;" + reader.getId() + ";" + sanitize(reader.getName()) + ";"
                            + sanitize(reader.getEmail()));
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving users: " + e.getMessage());
        }
    }

    public List<User> loadUsers() {
        List<User> users = new ArrayList<>();
        File file = new File(USERS_FILE_NAME);
        if (!file.exists()) {
            return users;
        }

        long lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
                IO_BUFFER_SIZE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 4) {
                    System.out.println("Skipping malformed user line " + lineNumber);
                    continue;
                }

                try {
                    String role = parts[0].trim();
                    int id = Integer.parseInt(parts[1].trim());
                    String name = parts[2];
                    String email = parts[3];

                    if ("MANAGER".equals(role) && parts.length >= 5) {
                        String employeeCode = parts[4];
                        users.add(new Manager(id, name, email, employeeCode));
                    } else if ("READER".equals(role)) {
                        users.add(new Reader(id, name, email));
                    } else {
                        System.out.println("Unknown or incomplete user on line " + lineNumber);
                    }
                } catch (NumberFormatException e) {
                    System.out.println("Invalid number on user line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading users: " + e.getMessage());
        }
        return users;
    }

    // Kulon txt fajl (library_loans.txt):
    // LOAN;loanId;readerId;readerName;itemId;borrowDate;dueDate
    public void saveActiveLoans(List<Loan> loans) {
        try (BufferedWriter bw = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(LOANS_FILE_NAME), StandardCharsets.UTF_8),
                IO_BUFFER_SIZE);
             PrintWriter writer = new PrintWriter(bw)) {
            for (Loan loan : loans) {
                if (!loan.isReturned() && loan.getReader() != null && loan.getItem() != null) {
                    writer.println("LOAN;" + loan.getLoanId() + ";"
                            + loan.getReader().getId() + ";" + sanitize(loan.getReader().getName()) + ";"
                            + loan.getItem().getId() + ";"
                            + loan.getBorrowDate() + ";" + loan.getDueDate());
                }
            }
        } catch (IOException e) {
            System.out.println("Error saving active loans: " + e.getMessage());
        }
    }

    public LoadedLoansData loadActiveLoans(Map<Integer, LibraryItem> itemById, Map<Integer, User> userById) {
        List<Loan> restoredLoans = new ArrayList<>();
        int maxLoanId = 0;

        File file = new File(LOANS_FILE_NAME);
        if (!file.exists()) {
            return new LoadedLoansData(restoredLoans, maxLoanId);
        }

        long lineNumber = 0;
        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8),
                IO_BUFFER_SIZE)) {
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                if (line.isBlank()) continue;

                String[] parts = line.split(";", -1);
                if (parts.length < 7) {
                    System.out.println("Skipping malformed loan line " + lineNumber);
                    continue;
                }

                try {
                    String recordType = parts[0].trim();
                    if (!"LOAN".equals(recordType)) {
                        System.out.println("Unknown record type on loan line " + lineNumber + ": " + recordType);
                        continue;
                    }

                    int loanId = Integer.parseInt(parts[1].trim());
                    int readerId = Integer.parseInt(parts[2].trim());
                    // parts[3] a reader neve olvashatosag/ellenorzes miatt a fajlban
                    int itemId = Integer.parseInt(parts[4].trim());
                    LocalDate borrowDate = LocalDate.parse(parts[5].trim());
                    LocalDate dueDate = LocalDate.parse(parts[6].trim());

                    User user = userById.get(readerId);
                    if (!(user instanceof Reader loanReader)) {
                        System.out.println("Skipping LOAN on line " + lineNumber + ": unknown or non-reader user ID " + readerId);
                        continue;
                    }

                    LibraryItem item = itemById.get(itemId);
                    if (item == null) {
                        System.out.println("Skipping LOAN on line " + lineNumber + ": unknown item ID " + itemId);
                        continue;
                    }

                    Loan loan = new Loan(loanId, loanReader, item, borrowDate, dueDate);
                    loanReader.addLoan(loan);
                    restoredLoans.add(loan);
                    if (loanId > maxLoanId) {
                        maxLoanId = loanId;
                    }
                } catch (Exception e) {
                    System.out.println("Invalid loan data on line " + lineNumber + ": " + e.getMessage());
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading active loans: " + e.getMessage());
        }

        return new LoadedLoansData(restoredLoans, maxLoanId);
    }

    private String sanitize(String value) {
        if (value == null) return "";
        return value.replace(";", ",").replace("\r", "").replace("\n", " ");
    }

    public static class LoadedLoansData {
        private final List<Loan> activeLoans;
        private final int maxLoanId;

        public LoadedLoansData(List<Loan> activeLoans, int maxLoanId) {
            this.activeLoans = activeLoans;
            this.maxLoanId = maxLoanId;
        }

        public List<Loan> getActiveLoans() {
            return activeLoans;
        }

        public int getMaxLoanId() {
            return maxLoanId;
        }
    }
}

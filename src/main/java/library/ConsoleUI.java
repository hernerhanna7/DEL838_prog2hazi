package library;

import java.util.List;
import java.util.Scanner;

public class ConsoleUI {
    private Library library;
    private Scanner scanner;
    private User currentUser;

    public ConsoleUI(Library library) {
        this.library = library;
        this.scanner = new Scanner(System.in);
    }

    public void start() {
        System.out.println("=== KONYVTAR RENDSZER ===");
        selectUser();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1" -> listAllBooks();
                    case "2" -> searchBooks();
                    case "3" -> borrowBook();
                    case "4" -> returnBook();
                    case "5" -> showMyLoans();
                    case "6" -> addBookMenu();
                    case "7" -> addCopiesMenu();
                    case "8" -> removeBookMenu();
                    case "9" -> selectUser();
                    case "10" -> registerReaderMenu();
                    case "0" -> {
                        running = false;
                        System.out.println("Kilepes... Viszlat!");
                    }
                    default -> System.out.println("Ervenytelen opcio!");
                }
            } catch (LibraryException e) {
                System.out.println("[HIBA] " + e.getMessage());
            } catch (NumberFormatException e) {
                System.out.println("[HIBA] Kerlek ervenyes szamot adj meg!");
            }
        }
    }

    private void selectUser() {
        while (true) {
            System.out.println("\nValassz felhasznalot (vagy irj 'R'-t uj Reader regisztraciojahoz):");
            for (User u : library.getUsers()) {
                u.printUserInfo();
            }
            System.out.print("Felhasznalo ID vagy 'R': ");
            String input = scanner.nextLine().trim();

            if ("R".equalsIgnoreCase(input)) {
                try {
                    Reader newReader = createReaderFromInput();
                    currentUser = newReader;
                    System.out.println("Sikeres regisztracio! Bejelentkezve mint: "
                            + currentUser.getName() + " (ID: " + currentUser.getId() + ", " + currentUser.getRoleName() + ")");
                    break;
                } catch (LibraryException e) {
                    System.out.println("[HIBA] " + e.getMessage());
                }
                continue;
            }

            try {
                int id = Integer.parseInt(input);
                User selected = library.findUserById(id);
                if (selected != null) {
                    currentUser = selected;
                    System.out.println("Bejelentkezve mint: " + currentUser.getName() + " (" + currentUser.getRoleName() + ")");
                    break;
                }
                System.out.println("Nincs ilyen ID-ju felhasznalo!");
            } catch (NumberFormatException e) {
                System.out.println("Ervenyes ID-t vagy 'R' betut adj meg!");
            }
        }
    }

    private void registerReaderMenu() throws LibraryException {
        Reader newReader = createReaderFromInput();
        System.out.println("Uj Reader sikeresen regisztralva es elmentve! ID: " + newReader.getId() + " (" + newReader.getName() + ")");
        System.out.print("At szeretnel jelentkezni az uj fiokba? (i/n): ");
        if ("i".equalsIgnoreCase(scanner.nextLine().trim())) {
            currentUser = newReader;
            System.out.println("Bejelentkezve mint: " + currentUser.getName());
        }
    }

    private Reader createReaderFromInput() throws LibraryException {
        System.out.print("Uj olvaso neve: ");
        String name = scanner.nextLine().trim();
        System.out.print("Email cime: ");
        String email = scanner.nextLine().trim();
        return library.registerNewReader(name, email);
    }

    private void printMenu() {
        System.out.println("\n--- MENU (" + currentUser.getName() + " - " + currentUser.getRoleName() + ") ---");
        System.out.println("1. Osszes konyv listazasa (ABC sorrendben)");
        System.out.println("2. Konyv keresese (Cim vagy Szerzo szerint)");
        if (!currentUser.canManageItems()) {
            System.out.println("3. Konyv kolcsonzese");
            System.out.println("4. Konyv visszavetel");
            System.out.println("5. Sajat aktiv kolcsonzeseim");
        }
        if (currentUser.canManageItems()) {
            System.out.println("6. [MANAGER] Uj konyv hozzaadasa");
            System.out.println("7. [MANAGER] Peldanyszam novelese");
            System.out.println("8. [MANAGER] Konyv torlese");
        }
        System.out.println("9. Felhasznalo valtasa");
        System.out.println("10. Uj Reader (olvaso) regisztralasa");
        System.out.println("0. Kilepes");
        System.out.print("Valasztas: ");
    }

    private void listAllBooks() {
        List<LibraryItem> items = library.getAllItemsSortedByTitle();
        if (items.isEmpty()) {
            System.out.println("A konyvtar jelenleg ures.");
            return;
        }
        for (LibraryItem item : items) {
            item.printInfo();
        }
    }

    private void searchBooks() {
        System.out.print("Kereses alapja (1 - Cim, 2 - Szerzo): ");
        String mode = scanner.nextLine().trim();
        if ("2".equals(mode)) {
            library.setSearchStrategy(new AuthorSearchStrategy());
        } else {
            library.setSearchStrategy(new TitleSearchStrategy());
        }

        System.out.print("Keresesi kifejezes: ");
        String query = scanner.nextLine().trim();
        List<LibraryItem> results = library.searchItems(query);

        if (results.isEmpty()) {
            System.out.println("Nincs talalat.");
        } else {
            System.out.println("Talalatok (" + results.size() + " db):");
            for (LibraryItem item : results) {
                item.printInfo();
            }
        }
    }

    private void borrowBook() throws LibraryException {
        System.out.print("Add meg a kolcsonozni kivant konyv ID-jat: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        Loan loan = library.borrowItem(currentUser, id);
        System.out.println("Sikeres kolcsonzes! Hatarido: " + loan.getDueDate());
    }

    private void returnBook() throws LibraryException {
        showMyLoans();
        System.out.print("Add meg a visszahozott kolcsonzes Loan ID-jat: ");
        int loanId = Integer.parseInt(scanner.nextLine().trim());
        double fee = library.returnItem(currentUser, loanId);
        System.out.println("Konyv visszaverve. Kesedelmi dij: " + fee + " EUR");
    }

    private void showMyLoans() {
        if (currentUser instanceof Reader reader) {
            List<Loan> active = reader.getActiveLoans();
            if (active.isEmpty()) {
                System.out.println("Nincs aktiv kolcsonzesed.");
                return;
            }
            for (Loan loan : active) {
                loan.printLoanInfo();
            }
        } else {
            System.out.println("Managerkent nincsenek sajat kolcsonzeseid.");
        }
    }

    private void addBookMenu() throws UnauthorizedActionException {
        if (!currentUser.canManageItems()) {
            throw new UnauthorizedActionException("Csak Manager adhat hozza konyvet!");
        }

        System.out.print("Tipus (1 - Nyomtatott konyv, 2 - E-konyv): ");
        String type = scanner.nextLine().trim();

        System.out.print("Cim: ");
        String title = scanner.nextLine().trim();
        System.out.print("Szerzo: ");
        String author = scanner.nextLine().trim();
        System.out.print("Kiadas eve: ");
        int year = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Peldanyszam: ");
        int copies = Integer.parseInt(scanner.nextLine().trim());

        LibraryItem newItem = null;
        if ("1".equals(type)) {
            System.out.print("ISBN: ");
            String isbn = scanner.nextLine().trim();
            System.out.print("Oldalszam: ");
            int pages = Integer.parseInt(scanner.nextLine().trim());
            newItem = ItemFactory.createPrintedBook(title, author, year, copies, isbn, pages);
        } else if ("2".equals(type)) {
            System.out.print("Formatum (pl. PDF, EPUB): ");
            String format = scanner.nextLine().trim();
            System.out.print("Fajlmeret (MB): ");
            double size = Double.parseDouble(scanner.nextLine().trim());
            newItem = ItemFactory.createEBook(title, author, year, copies, format, size);
        }

        if (newItem == null) {
            System.out.println("Hibas adatok! A konyv letrehozasa sikertelen.");
            return;
        }

        if (library.addItem(currentUser, newItem)) {
            System.out.println("Konyv sikeresen hozzaadva! ID: " + newItem.getId());
        } else {
            System.out.println("Nem sikerult hozzaadni a konyvet (megtelt a konyvtar vagy mar letezik).");
        }
    }

    private void addCopiesMenu() throws UnauthorizedActionException {
        System.out.print("Konyv ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        System.out.print("Hozzaadando peldanyszam: ");
        int count = Integer.parseInt(scanner.nextLine().trim());
        if (library.addCopies(currentUser, id, count)) {
            System.out.println("Peldanyszam sikeresen frissitve.");
        } else {
            System.out.println("Sikertelen frissites.");
        }
    }

    private void removeBookMenu() throws UnauthorizedActionException {
        System.out.print("Torolni kivant konyv ID: ");
        int id = Integer.parseInt(scanner.nextLine().trim());
        if (library.removeItem(currentUser, id)) {
            System.out.println("Konyv sikeresen torolve.");
        } else {
            System.out.println("Nem torolheto (nem letezik vagy aktiv kolcsonzes van ra).");
        }
    }
}

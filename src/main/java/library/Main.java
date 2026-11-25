package library;

public class Main {
    public static void main(String[] args) {
        Library library = new Library();

        if (library.getUsers().isEmpty()) {
            Manager manager = new Manager(1, "Kovacs Anna", "anna.kovacs@library.hu", "EMP-01");
            Reader reader1 = new Reader(2, "Nagy Peter", "peter.nagy@gmail.com");
            Reader reader2 = new Reader(3, "Szabo Eszter", "eszter.szabo@gmail.com");

            library.registerUser(manager);
            library.registerUser(reader1);
            library.registerUser(reader2);
        }

        if (library.getAllItemsSortedByTitle().isEmpty()) {
            User firstManager = null;
            for (User u : library.getUsers()) {
                if (u.canManageItems()) {
                    firstManager = u;
                    break;
                }
            }
            if (firstManager != null) {
                try {
                    library.addItem(firstManager, ItemFactory.createPrintedBook(
                            "Clean Code", "Robert C. Martin", 2008, 3, "978-0132350884", 464));
                    library.addItem(firstManager, ItemFactory.createPrintedBook(
                            "Effective Java", "Joshua Bloch", 2018, 2, "978-0134685991", 416));
                    library.addItem(firstManager, ItemFactory.createEBook(
                            "Design Patterns", "Erich Gamma", 1994, 5, "PDF", 12.4));
                } catch (UnauthorizedActionException e) {
                    System.out.println(e.getMessage());
                }
            }
        }

        ConsoleUI ui = new ConsoleUI(library);
        ui.start();
    }
}

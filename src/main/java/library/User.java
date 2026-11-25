package library;

public abstract class User {
    private int id;
    private String name;
    private String email;

    public User(int id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public abstract boolean canManageItems();

    public abstract String getRoleName();

    public void printUserInfo() {
        System.out.println("[" + getRoleName() + "] ID: " + id + " | Name: " + name + " | Email: " + email);
    }
}

package library;

public class Manager extends User {
    private String employeeCode;

    public Manager(int id, String name, String email, String employeeCode) {
        super(id, name, email);
        this.employeeCode = employeeCode;
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    @Override
    public boolean canManageItems() {
        return true;
    }

    @Override
    public String getRoleName() {
        return "MANAGER";
    }
}

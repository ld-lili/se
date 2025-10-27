public class Employee extends User {
    private String employeeId;
    private String name;

    public Employee(String username, String password, String employeeId, String name) {
        super(username, password);
        this.employeeId = employeeId;
        this.name = name;
    }

    // Getters and Setters
    public String getEmployeeId() {
        return employeeId;
    }

    public void setEmployeeId(String employeeId) {
        this.employeeId = employeeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getRole() {
        return "Employee";
    }

    @Override
    public String toString() {
        return "Employee{employeeId='" + employeeId + "', name='" + name +
                "', username='" + getUsername() + "', active=" + isActive() + "}";
    }
}
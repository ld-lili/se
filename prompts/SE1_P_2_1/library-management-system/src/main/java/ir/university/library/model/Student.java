package ir.university.library.model;

public class Student extends User {
    private String studentId;
    private String name;
    private String email;

    public Student(String username, String password, String studentId, String name, String email) {
        super(username, password);
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    // Getters and Setters
    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String getRole() {
        return "Student";
    }

    @Override
    public String toString() {
        return "Student{studentId='" + studentId + "', name='" + name + "', email='" + email +
                "', username='" + getUsername() + "', active=" + isActive() + "}";
    }
}
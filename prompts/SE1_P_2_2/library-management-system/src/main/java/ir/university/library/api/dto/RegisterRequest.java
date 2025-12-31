package ir.university.library.api.dto;

public class RegisterRequest {
    private String username;
    private String password;
    private String studentId;
    private String name;
    private String email;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String password, String studentId, String name, String email) {
        this.username = username;
        this.password = password;
        this.studentId = studentId;
        this.name = name;
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

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
}
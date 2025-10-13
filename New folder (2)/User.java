import java.io.Serializable;

public abstract class User implements Serializable {
    private String username;
    private String password;
    private boolean active;

    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.active = true;
    }

    // Getters and Setters
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

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public abstract String getRole();

    @Override
    public String toString() {
        return getRole() + "{username='" + username + "', active=" + active + "}";
    }
}
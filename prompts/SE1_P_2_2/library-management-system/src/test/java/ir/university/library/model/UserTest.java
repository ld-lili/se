package ir.university.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("User Model Tests")
class UserTest {

    @Test
    @DisplayName("Should create user with correct properties")
    void testUserCreation() {
        TestUser user = new TestUser("testuser", "password123");

        assertEquals("testuser", user.getUsername());
        assertEquals("password123", user.getPassword());
        assertTrue(user.isActive());
        assertEquals("TestUser", user.getRole());
    }

    @Test
    @DisplayName("Should change user status")
    void testUserStatus() {
        TestUser user = new TestUser("testuser", "password123");

        assertTrue(user.isActive());
        user.setActive(false);
        assertFalse(user.isActive());
    }

    @Test
    @DisplayName("Should change password")
    void testPasswordChange() {
        TestUser user = new TestUser("testuser", "password123");

        assertEquals("password123", user.getPassword());
        user.setPassword("newpassword");
        assertEquals("newpassword", user.getPassword());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        TestUser user = new TestUser("testuser", "password123");
        String result = user.toString();

        assertTrue(result.contains("testuser"));
        assertTrue(result.contains("TestUser"));
    }

    // کلاس تست برای User انتزاعی
    private static class TestUser extends User {
        public TestUser(String username, String password) {
            super(username, password);
        }

        @Override
        public String getRole() {
            return "TestUser";
        }
    }
}
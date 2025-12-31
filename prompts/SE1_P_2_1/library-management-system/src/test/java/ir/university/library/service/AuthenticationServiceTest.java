package ir.university.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import ir.university.library.model.Student;
import ir.university.library.model.Employee; // این import را اضافه کنید

class AuthenticationServiceTest {

    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager();
        libraryManager.clearAllData();
    }

    @Test
    @DisplayName("1-1: ثبت‌نام کاربر جدید با نام کاربری منحصربه‌فرد")
    void registerNewUserWithUniqueUsername() {
        // Arrange
        String username = "newuser";
        String password = "password123";
        String studentId = "S12345";
        String name = "محمد جدید";
        String email = "newuser@university.ac.ir";

        // Act
        boolean result = libraryManager.registerStudent(username, password, studentId, name, email);

        // Assert
        assertTrue(result, "ثبت‌نام باید موفق باشد");
        assertNotNull(libraryManager.findStudentByUsername(username), "کاربر باید در سیستم وجود داشته باشد");

        Student registeredStudent = libraryManager.findStudentByUsername(username);
        assertEquals(username, registeredStudent.getUsername());
        assertEquals(studentId, registeredStudent.getStudentId());
        assertEquals(name, registeredStudent.getName());
        assertEquals(email, registeredStudent.getEmail());
        assertTrue(registeredStudent.isActive(), "کاربر جدید باید فعال باشد");
    }

    @Test
    @DisplayName("1-2: ثبت‌نام با نام کاربری تکراری")
    void registerUserWithDuplicateUsername() {
        // Arrange
        String username = "duplicateuser";
        String password = "pass123";
        String studentId1 = "S10001";
        String studentId2 = "S10002";
        String name = "کاربر تست";
        String email = "test@university.ac.ir";

        // ثبت کاربر اول
        boolean firstResult = libraryManager.registerStudent(username, password, studentId1, name, email);
        assertTrue(firstResult, "ثبت اول باید موفق باشد");

        // Act - ثبت دوم با نام کاربری تکراری
        boolean secondResult = libraryManager.registerStudent(username, "differentpass", studentId2, "کاربر دیگر",
                "other@university.ac.ir");

        // Assert
        assertFalse(secondResult, "ثبت با نام کاربری تکراری باید ناموفق باشد");

        // بررسی تعداد دانشجویان
        assertEquals(1, libraryManager.getStudents().size(), "فقط باید یک کاربر ثبت شده باشد");
    }

    @Test
    @DisplayName("1-2-b: ثبت‌نام با شماره دانشجویی تکراری")
    void registerUserWithDuplicateStudentId() {
        // Arrange
        String username1 = "user1";
        String username2 = "user2";
        String password = "pass123";
        String studentId = "S10001"; // شماره دانشجویی تکراری
        String name1 = "کاربر اول";
        String name2 = "کاربر دوم";
        String email1 = "user1@university.ac.ir";
        String email2 = "user2@university.ac.ir";

        // ثبت کاربر اول
        boolean firstResult = libraryManager.registerStudent(username1, password, studentId, name1, email1);
        assertTrue(firstResult, "ثبت اول باید موفق باشد");

        // Act - ثبت دوم با شماره دانشجویی تکراری
        boolean secondResult = libraryManager.registerStudent(username2, password, studentId, name2, email2);

        // Assert
        assertFalse(secondResult, "ثبت با شماره دانشجویی تکراری باید ناموفق باشد");

        // بررسی تعداد
        assertEquals(1, libraryManager.getStudents().size(), "فقط باید یک کاربر ثبت شده باشد");
    }

    @Test
    @DisplayName("1-3: ورود با نام کاربری و رمز عبور صحیح")
    void loginWithValidCredentials() {
        // Arrange
        String username = "validuser";
        String password = "correctpass";
        String studentId = "S10003";
        String name = "کاربر معتبر";
        String email = "valid@university.ac.ir";

        // ثبت کاربر
        boolean registerResult = libraryManager.registerStudent(username, password, studentId, name, email);
        assertTrue(registerResult, "ثبت‌نام باید موفق باشد");

        // Act - ورود
        boolean loginResult = libraryManager.studentLogin(username, password);

        // Assert
        assertTrue(loginResult, "ورود باید موفق باشد");
        assertNotNull(libraryManager.getCurrentUser(), "کاربر جاری باید تنظیم شود");
        assertEquals(username, libraryManager.getCurrentUser().getUsername());
        assertTrue(libraryManager.getCurrentUser().isActive(), "کاربر باید فعال باشد");
    }

    @Test
    @DisplayName("1-4: ورود با نام کاربری صحیح اما رمز عبور نادرست")
    void loginWithCorrectUsernameButWrongPassword() {
        // Arrange
        String username = "user123";
        String correctPassword = "rightpass";
        String wrongPassword = "wrongpass";
        String studentId = "S10004";
        String name = "کاربر تست";
        String email = "test@university.ac.ir";

        // ثبت کاربر با رمز صحیح
        boolean registerResult = libraryManager.registerStudent(username, correctPassword, studentId, name, email);
        assertTrue(registerResult, "ثبت‌نام باید موفق باشد");

        // Act - ورود با رمز اشتباه
        boolean loginResult = libraryManager.studentLogin(username, wrongPassword);

        // Assert
        assertFalse(loginResult, "ورود با رمز اشتباه باید ناموفق باشد");
        assertNull(libraryManager.getCurrentUser(), "کاربر جاری نباید تنظیم شود");
    }

    @Test
    @DisplayName("1-5: ورود با نام کاربری که وجود ندارد")
    void loginWithNonExistentUsername() {
        // Arrange
        String username = "nonexistent";
        String password = "anypassword";

        // Act
        boolean result = libraryManager.studentLogin(username, password);

        // Assert
        assertFalse(result, "ورود با کاربر ناموجود باید ناموفق باشد");
        assertNull(libraryManager.getCurrentUser(), "کاربر جاری نباید تنظیم شود");
    }

    @Test
    @DisplayName("1-6: ورود کاربر غیرفعال")
    void loginWithInactiveUser() {
        // Arrange
        String username = "inactiveuser";
        String password = "pass123";
        String studentId = "S10005";
        String name = "کاربر غیرفعال";
        String email = "inactive@university.ac.ir";

        // ثبت کاربر
        boolean registerResult = libraryManager.registerStudent(username, password, studentId, name, email);
        assertTrue(registerResult, "ثبت‌نام باید موفق باشد");

        // غیرفعال کردن کاربر
        boolean toggleResult = libraryManager.toggleStudentStatus(username);
        assertTrue(toggleResult, "تغییر وضعیت باید موفق باشد");

        // Act
        boolean loginResult = libraryManager.studentLogin(username, password);

        // Assert
        assertFalse(loginResult, "ورود کاربر غیرفعال باید ناموفق باشد");
        assertNull(libraryManager.getCurrentUser(), "کاربر جاری نباید تنظیم شود");
    }

    @Test
    @DisplayName("ورود کارمند")
    void employeeLoginTest() {
        // Arrange
        String username = "testemp";
        String password = "emppass";
        String employeeId = "E001";
        String name = "کارمند تست";

        // اضافه کردن کارمند
        Employee employee = new Employee(username, password, employeeId, name);
        boolean addResult = libraryManager.addEmployee(employee);
        assertTrue(addResult, "اضافه کردن کارمند باید موفق باشد");

        // Act
        boolean loginResult = libraryManager.employeeLogin(username, password);

        // Assert
        assertTrue(loginResult, "ورود کارمند باید موفق باشد");
        assertNotNull(libraryManager.getCurrentUser(), "کاربر جاری باید تنظیم شود");
        assertEquals(username, libraryManager.getCurrentUser().getUsername());
        assertEquals("Employee", libraryManager.getCurrentUser().getRole());
    }
}
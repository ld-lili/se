package ir.university.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.io.TempDir;
import static org.junit.jupiter.api.Assertions.*;

import ir.university.library.model.*;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.List;

@DisplayName("Library Manager Service Tests")
class LibraryManagerTest {

    @TempDir
    Path tempDir;

    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager();
        // پاکسازی داده‌های تست
        clearTestData();
    }

    private void clearTestData() {
        // پاک کردن دانشجویان تست
        libraryManager.getStudents().removeIf(student -> student.getUsername().startsWith("test_"));
        // پاک کردن کارمندان تست
        libraryManager.getEmployees().removeIf(employee -> employee.getUsername().startsWith("test_"));
        // پاک کردن کتاب‌های تست
        libraryManager.getBooks().removeIf(book -> book.getBookId().startsWith("TEST_"));
        // پاک کردن امانت‌های تست
        libraryManager.getLoans().removeIf(loan -> loan.getLoanId().startsWith("TEST_"));
    }

    @Test
    @DisplayName("Should add employee successfully")
    void testAddEmployeeSuccess() {
        Employee employee = new Employee("test_emp1", "password123", "TEST_E001", "Test Employee");

        boolean result = libraryManager.addEmployee(employee);

        assertTrue(result);

        // بررسی وجود کارمند
        Employee found = libraryManager.findEmployeeByUsername("test_emp1");
        assertNotNull(found);
        assertEquals("TEST_E001", found.getEmployeeId());
        assertEquals("Test Employee", found.getName());
    }

    @Test
    @DisplayName("Should reject duplicate employee username")
    void testAddEmployeeDuplicateUsername() {
        Employee emp1 = new Employee("test_emp_dup", "pass1", "TEST_E100", "Employee One");
        Employee emp2 = new Employee("test_emp_dup", "pass2", "TEST_E101", "Employee Two");

        boolean result1 = libraryManager.addEmployee(emp1);
        boolean result2 = libraryManager.addEmployee(emp2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Should reject duplicate employee ID")
    void testAddEmployeeDuplicateId() {
        Employee emp1 = new Employee("test_emp1", "pass1", "TEST_E200", "Employee One");
        Employee emp2 = new Employee("test_emp2", "pass2", "TEST_E200", "Employee Two");

        boolean result1 = libraryManager.addEmployee(emp1);
        boolean result2 = libraryManager.addEmployee(emp2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Should register student successfully")
    void testStudentRegistrationSuccess() {
        boolean result = libraryManager.registerStudent(
                "test_student_real", "password123", "TEST_S999", "Test Student", "test@university.edu");

        assertTrue(result);

        // بررسی وجود دانشجو
        Student student = libraryManager.findStudentByUsername("test_student_real");
        assertNotNull(student);
        assertEquals("TEST_S999", student.getStudentId());
        assertEquals("Test Student", student.getName());
        assertEquals("test@university.edu", student.getEmail());
    }

    @Test
    @DisplayName("Should reject duplicate student username")
    void testStudentRegistrationDuplicateUsername() {
        libraryManager.registerStudent("test_dup_user", "pass1", "TEST_S100", "Student One", "one@test.edu");

        boolean result = libraryManager.registerStudent("test_dup_user", "pass2", "TEST_S101", "Student Two",
                "two@test.edu");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should reject duplicate student ID")
    void testStudentRegistrationDuplicateId() {
        libraryManager.registerStudent("test_user1", "pass1", "TEST_S200", "Student One", "one@test.edu");

        boolean result = libraryManager.registerStudent("test_user2", "pass2", "TEST_S200", "Student Two",
                "two@test.edu");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should login student successfully")
    void testStudentLoginSuccess() {
        libraryManager.registerStudent("test_login", "testpass", "TEST_LOGIN", "Login Test", "login@test.edu");

        boolean loginResult = libraryManager.studentLogin("test_login", "testpass");

        assertTrue(loginResult);
        assertNotNull(libraryManager.getCurrentUser());
        assertEquals("test_login", libraryManager.getCurrentUser().getUsername());
    }

    @Test
    @DisplayName("Should reject student login with wrong password")
    void testStudentLoginWrongPassword() {
        libraryManager.registerStudent("test_login", "testpass", "TEST_LOGIN", "Login Test", "login@test.edu");

        boolean failedLogin = libraryManager.studentLogin("test_login", "wrongpass");

        assertFalse(failedLogin);
        assertNull(libraryManager.getCurrentUser());
    }

    @Test
    @DisplayName("Should login employee successfully")
    void testEmployeeLoginSuccess() {
        Employee emp = new Employee("test_emplogin", "emppass", "TEST_EMP_LOGIN", "Employee Login");
        libraryManager.addEmployee(emp);

        boolean loginResult = libraryManager.employeeLogin("test_emplogin", "emppass");

        assertTrue(loginResult);
        assertNotNull(libraryManager.getCurrentUser());
        assertEquals("test_emplogin", libraryManager.getCurrentUser().getUsername());
    }

    @Test
    @DisplayName("Should add book successfully")
    void testAddBookSuccess() {
        Book book = new Book("TEST_BOOK_001", "Test Book Title", "Test Author",
                "Test Publisher", 2024, "TEST_ISBN_001");

        boolean result = libraryManager.addBook(book);

        assertTrue(result);

        Book foundBook = libraryManager.findBookById("TEST_BOOK_001");
        assertNotNull(foundBook);
        assertEquals("Test Book Title", foundBook.getTitle());
        assertTrue(foundBook.isAvailable());
    }

    @Test
    @DisplayName("Should reject duplicate book ID")
    void testAddBookDuplicateId() {
        Book book1 = new Book("TEST_DUP_ID", "Book One", "Author One", "Pub One", 2023, "ISBN1");
        Book book2 = new Book("TEST_DUP_ID", "Book Two", "Author Two", "Pub Two", 2024, "ISBN2");

        boolean result1 = libraryManager.addBook(book1);
        boolean result2 = libraryManager.addBook(book2);

        assertTrue(result1);
        assertFalse(result2);
    }

    @Test
    @DisplayName("Should search books by title")
    void testBookSearchByTitle() {
        Book book1 = new Book("TEST_SEARCH1", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567890");
        Book book2 = new Book("TEST_SEARCH2", "Python Programming", "Jane Smith", "Tech Pub", 2024, "1234567891");

        libraryManager.addBook(book1);
        libraryManager.addBook(book2);

        List<Book> results = libraryManager.searchBooks("Java", null, null);

        assertEquals(1, results.size());
        assertEquals("Java Programming", results.get(0).getTitle());
    }

    @Test
    @DisplayName("Should search books by author")
    void testBookSearchByAuthor() {
        Book book1 = new Book("TEST_SEARCH3", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567892");
        Book book2 = new Book("TEST_SEARCH4", "Python Programming", "Jane Smith", "Tech Pub", 2024, "1234567893");

        libraryManager.addBook(book1);
        libraryManager.addBook(book2);

        List<Book> results = libraryManager.searchBooks(null, "John", null);

        assertEquals(1, results.size());
        assertEquals("John Doe", results.get(0).getAuthor());
    }

    @Test
    @DisplayName("Should search books by year")
    void testBookSearchByYear() {
        Book book1 = new Book("TEST_SEARCH5", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567894");
        Book book2 = new Book("TEST_SEARCH6", "Python Programming", "Jane Smith", "Tech Pub", 2024, "1234567895");

        libraryManager.addBook(book1);
        libraryManager.addBook(book2);

        List<Book> results = libraryManager.searchBooks(null, null, 2023);

        assertEquals(1, results.size());
        assertEquals(2023, results.get(0).getPublicationYear());
    }

    @Test
    @DisplayName("Should toggle student status")
    void testToggleStudentStatus() {
        libraryManager.registerStudent("test_toggle", "pass", "TEST_TOGGLE", "Toggle Test", "toggle@test.edu");

        Student student = libraryManager.findStudentByUsername("test_toggle");
        assertTrue(student.isActive());

        // غیرفعال کردن
        boolean toggle1 = libraryManager.toggleStudentStatus("test_toggle");
        assertTrue(toggle1);
        assertFalse(student.isActive());

        // فعال کردن مجدد
        boolean toggle2 = libraryManager.toggleStudentStatus("test_toggle");
        assertTrue(toggle2);
        assertTrue(student.isActive());
    }

    @Test
    @DisplayName("Should return false for non-existent student toggle")
    void testToggleNonExistentStudent() {
        boolean result = libraryManager.toggleStudentStatus("nonexistent");

        assertFalse(result);
    }

    @Test
    @DisplayName("Should return student statistics")
    void testGetStudentStatistics() {
        libraryManager.registerStudent("test_stats", "statspass", "TEST_STATS", "Stats Test", "stats@test.edu");

        var stats = libraryManager.getStudentStatistics("test_stats");

        assertNotNull(stats);
        assertEquals(0, stats.getTotalLoans());
        assertEquals(0, stats.getNotReturned());
        assertEquals(0, stats.getDelayedReturns());
    }

    @Test
    @DisplayName("Should return loan statistics")
    void testGetLoanStatistics() {
        var stats = libraryManager.getLoanStatistics();

        assertNotNull(stats);
        assertTrue(stats.getTotalRequests() >= 0);
        assertTrue(stats.getTotalApproved() >= 0);
        assertTrue(stats.getAverageLoanDays() >= 0);
    }

    @Test
    @DisplayName("Should process complete loan workflow")
    void testCompleteLoanWorkflow() {
        // ثبت دانشجو
        libraryManager.registerStudent("test_loan", "loanpass", "TEST_LOAN", "Loan Test", "loan@test.edu");
        // اضافه کردن کتاب
        Book book = new Book("TEST_LOAN_BOOK", "Loan Book", "Loan Author", "Loan Pub", 2024, "LOAN_ISBN");
        libraryManager.addBook(book);

        // لاگین دانشجو
        libraryManager.studentLogin("test_loan", "loanpass");

        // درخواست امانت
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = startDate.plusDays(14);
        boolean loanRequest = libraryManager.requestLoan("TEST_LOAN_BOOK", startDate, endDate);

        assertTrue(loanRequest);

        // بررسی درخواست‌های در انتظار
        var pendingLoans = libraryManager.getPendingLoans();
        assertFalse(pendingLoans.isEmpty());

        // بررسی وضعیت کتاب
        Book loanBook = libraryManager.findBookById("TEST_LOAN_BOOK");
        assertTrue(loanBook.isAvailable()); // هنوز در انتظار تایید است
    }
}
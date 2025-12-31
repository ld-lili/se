package ir.university.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import ir.university.library.model.*;
import ir.university.library.exception.*;

class LoanManagementServiceTest {

    private LibraryManager libraryManager;
    private Student activeStudent;
    private Book testBook;
    private Employee employee;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager();
        libraryManager.clearAllData();

        // ایجاد داده‌های تست تازه
        createFreshTestData();
    }

    private void createFreshTestData() {
        // ایجاد دانشجوی فعال
        activeStudent = new Student("activestu", "pass123", "S10001", "دانشجوی فعال", "active@university.ac.ir");

        // ایجاد کارمند
        employee = new Employee("emp1", "pass123", "E001", "کارمند تست");

        // ایجاد کتاب جدید (مطمئن شوید که در دسترس است)
        testBook = new Book("TEST001", "کتاب تست", "نویسنده تست", "ناشر تست", 2024, "ISBN-TEST-001");
        testBook.setAvailable(true); // مطمئن شوید که در دسترس است

        // اضافه کردن به لیست‌ها
        libraryManager.getStudents().add(activeStudent);
        libraryManager.getEmployees().add(employee);
        libraryManager.getBooks().add(testBook);
    }

    @Test
    @DisplayName("3-1: دانشجوی فعال برای یک کتاب موجود درخواست امانت می‌دهد")
    void activeStudentRequestsLoanForAvailableBook() {
        try {
            // Arrange
            libraryManager.setCurrentUser(activeStudent);
            String bookId = testBook.getBookId();

            // اطمینان از اینکه کتاب در دسترس است
            assertTrue(testBook.isAvailable(), "کتاب باید در دسترس باشد");

            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(14);

            // Act
            boolean result = libraryManager.requestLoan(bookId, startDate, endDate);

            // Assert
            assertTrue(result, "درخواست امانت باید موفق باشد");

            List<Loan> loans = libraryManager.getLoans();
            assertFalse(loans.isEmpty(), "باید حداقل یک امانت ایجاد شده باشد");

            Loan createdLoan = loans.get(0);
            assertEquals("PENDING", createdLoan.getStatus(), "وضعیت باید PENDING باشد");
            assertEquals(activeStudent.getUsername(), createdLoan.getStudentUsername());
            assertEquals(bookId, createdLoan.getBookId());

        } catch (Exception e) {
            fail("استثنایی نباید رخ دهد: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3-2: دانشجوی غیرفعال سعی می‌کند درخواست امانت بدهد")
    void inactiveStudentTriesToRequestLoan() {
        try {
            // Arrange
            Student inactiveStudent = new Student("inactivestu", "pass123", "S10002", "دانشجوی غیرفعال",
                    "inactive@university.ac.ir");
            inactiveStudent.setActive(false);
            libraryManager.getStudents().add(inactiveStudent);

            libraryManager.setCurrentUser(inactiveStudent);
            String bookId = testBook.getBookId();
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(14);

            // Act & Assert
            assertThrows(
                    InvalidStudentStatusException.class,
                    () -> libraryManager.requestLoan(bookId, startDate, endDate),
                    "باید استثنا برای دانشجوی غیرفعال پرتاب شود");

        } catch (Exception e) {
            fail("استثنای دیگری نباید رخ دهد: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3-3: درخواست امانت برای کتابی که وضعیت آن BORROWED است")
    void requestLoanForBorrowedBook() {
        try {
            // Arrange
            // کتابی ایجاد می‌کنیم که در دسترس نیست
            Book borrowedBook = new Book("BORROWED001", "کتاب امانت داده شده", "نویسنده", "ناشر", 2024,
                    "ISBN-BORROWED");
            borrowedBook.setAvailable(false); // در دسترس نیست
            libraryManager.getBooks().add(borrowedBook);

            libraryManager.setCurrentUser(activeStudent);
            String bookId = borrowedBook.getBookId();
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(14);

            // Act & Assert
            assertThrows(
                    BookNotAvailableException.class,
                    () -> libraryManager.requestLoan(bookId, startDate, endDate),
                    "باید استثنا برای کتاب غیرقابل دسترس پرتاب شود");

        } catch (Exception e) {
            fail("استثنای دیگری نباید رخ دهد: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3-4: تایید یک درخواست امانت معتبر")
    void approveValidLoanRequest() {
        try {
            // Arrange
            // کتاب جدید برای تست
            Book approveBook = new Book("APPROVE001", "کتاب تایید", "نویسنده", "ناشر", 2024, "ISBN-APPROVE");
            approveBook.setAvailable(true);
            libraryManager.getBooks().add(approveBook);

            // ایجاد درخواست امانت
            libraryManager.setCurrentUser(activeStudent);
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(14);
            boolean requestResult = libraryManager.requestLoan("APPROVE001", startDate, endDate);
            assertTrue(requestResult, "درخواست امانت باید موفق باشد");

            // بررسی امانت در انتظار
            List<Loan> pendingLoans = libraryManager.getPendingLoans();
            assertFalse(pendingLoans.isEmpty(), "باید امانت در انتظار وجود داشته باشد");

            Loan pendingLoan = pendingLoans.get(0);
            String loanId = pendingLoan.getLoanId();

            // به کارمند تغییر کاربر دهیم
            libraryManager.setCurrentUser(employee);

            // Act
            boolean result = libraryManager.approveLoan(loanId, employee.getUsername());

            // Assert
            assertTrue(result, "تایید درخواست امانت باید موفق باشد");

            Loan approvedLoan = libraryManager.getLoanById(loanId);
            assertNotNull(approvedLoan, "امانت باید پیدا شود");
            assertEquals("APPROVED", approvedLoan.getStatus(), "وضعیت باید APPROVED شود");
            assertEquals(employee.getUsername(), approvedLoan.getEmployeeUsername());

        } catch (Exception e) {
            fail("استثنایی نباید رخ دهد: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3-5: تلاش برای تایید یک درخواست که قبلاً تایید شده است")
    void tryToApproveAlreadyApprovedRequest() {
        try {
            // Arrange
            Book testBook2 = new Book("TEST002", "کتاب تست ۲", "نویسنده", "ناشر", 2024, "ISBN-TEST-002");
            testBook2.setAvailable(true);
            libraryManager.getBooks().add(testBook2);

            // ایجاد درخواست امانت
            libraryManager.setCurrentUser(activeStudent);
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(14);
            libraryManager.requestLoan("TEST002", startDate, endDate);

            // تایید اول
            libraryManager.setCurrentUser(employee);
            List<Loan> pendingLoans = libraryManager.getPendingLoans();
            assertFalse(pendingLoans.isEmpty(), "باید امانت در انتظار وجود داشته باشد");

            Loan testLoan = pendingLoans.get(0);
            String loanId = testLoan.getLoanId();

            boolean firstApprove = libraryManager.approveLoan(loanId, employee.getUsername());
            assertTrue(firstApprove, "تایید اول باید موفق باشد");

            // Act & Assert - تلاش برای تایید مجدد
            assertThrows(
                    InvalidRequestStatusException.class,
                    () -> libraryManager.approveLoan(loanId, employee.getUsername()),
                    "باید استثنا برای درخواست قبلاً تایید شده پرتاب شود");

        } catch (Exception e) {
            fail("استثنای دیگری نباید رخ دهد: " + e.getMessage());
        }
    }

    @Test
    @DisplayName("3-8: بازگشت کتاب")
    void returnBookTest() {
        try {
            // Arrange
            Book returnBook = new Book("RETURN001", "کتاب بازگشت", "نویسنده", "ناشر", 2024, "ISBN-RETURN");
            returnBook.setAvailable(true);
            libraryManager.getBooks().add(returnBook);

            // ایجاد و تایید درخواست امانت
            libraryManager.setCurrentUser(activeStudent);
            LocalDate startDate = LocalDate.now().plusDays(1);
            LocalDate endDate = LocalDate.now().plusDays(14);
            libraryManager.requestLoan("RETURN001", startDate, endDate);

            libraryManager.setCurrentUser(employee);
            List<Loan> pendingLoans = libraryManager.getPendingLoans();
            assertFalse(pendingLoans.isEmpty(), "باید امانت در انتظار وجود داشته باشد");

            Loan testLoan = pendingLoans.get(0);
            boolean approveResult = libraryManager.approveLoan(testLoan.getLoanId(), employee.getUsername());
            assertTrue(approveResult, "تایید امانت باید موفق باشد");

            // Act - بازگشت کتاب
            boolean result = libraryManager.returnBook(testLoan.getLoanId());

            // Assert
            assertTrue(result, "بازگشت کتاب باید موفق باشد");

            Loan returnedLoan = libraryManager.getLoanById(testLoan.getLoanId());
            assertEquals("RETURNED", returnedLoan.getStatus(), "وضعیت باید RETURNED شود");
            assertNotNull(returnedLoan.getActualReturnDate(), "تاریخ بازگشت باید ثبت شود");

        } catch (Exception e) {
            fail("استثنایی نباید رخ دهد: " + e.getMessage());
        }
    }
}
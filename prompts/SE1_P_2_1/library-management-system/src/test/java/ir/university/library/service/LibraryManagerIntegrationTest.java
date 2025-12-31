package ir.university.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import ir.university.library.model.*;

class LibraryManagerIntegrationTest {

    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager();
        libraryManager.clearAllData();
    }

    @Test
    @DisplayName("Integration: چرخه کامل امانت کتاب")
    void integrationTest_FullLoanCycle() {
        // 1. ثبت دانشجو
        boolean studentRegistered = libraryManager.registerStudent(
                "loancycle", "pass123", "S88888", "چرخه امانت", "loan@university.ac.ir");
        assertTrue(studentRegistered, "ثبت دانشجو باید موفق باشد");

        // 2. اضافه کردن کارمند
        Employee employee = new Employee("loanemp", "pass123", "E888", "کارمند امانت");
        boolean employeeAdded = libraryManager.addEmployee(employee);
        assertTrue(employeeAdded, "اضافه کردن کارمند باید موفق باشد");

        // 3. اضافه کردن کتاب
        Book testBook = new Book("CYC001", "کتاب چرخه", "نویسنده چرخه", "ناشر چرخه", 2024, "ISBN-CYCLE");
        boolean bookAdded = libraryManager.addBook(testBook);
        assertTrue(bookAdded, "اضافه کردن کتاب باید موفق باشد");

        // 4. دانشجو وارد می‌شود
        boolean studentLogin = libraryManager.studentLogin("loancycle", "pass123");
        assertTrue(studentLogin, "ورود دانشجو باید موفق باشد");

        // 5. درخواست امانت
        LocalDate startDate = LocalDate.now().plusDays(1);
        LocalDate endDate = LocalDate.now().plusDays(10);
        boolean loanRequested = libraryManager.requestLoan("CYC001", startDate, endDate);
        assertTrue(loanRequested, "درخواست امانت باید موفق باشد");

        // 6. بررسی درخواست در انتظار
        List<Loan> pendingLoans = libraryManager.getPendingLoans();
        assertEquals(1, pendingLoans.size(), "باید یک امانت در انتظار باشد");

        Loan createdLoan = pendingLoans.get(0);
        assertEquals("PENDING", createdLoan.getStatus());
        assertEquals("loancycle", createdLoan.getStudentUsername());
        assertEquals("CYC001", createdLoan.getBookId());

        // 7. کارمند وارد می‌شود
        boolean employeeLogin = libraryManager.employeeLogin("loanemp", "pass123");
        assertTrue(employeeLogin, "ورود کارمند باید موفق باشد");

        // 8. تایید امانت
        String loanId = createdLoan.getLoanId();
        boolean loanApproved = libraryManager.approveLoan(loanId, "loanemp");
        assertTrue(loanApproved, "تایید امانت باید موفق باشد");

        // 9. بررسی وضعیت کتاب بعد از تایید
        Book bookAfterApproval = libraryManager.findBookById("CYC001");
        assertNotNull(bookAfterApproval, "کتاب باید پیدا شود");
        assertFalse(bookAfterApproval.isAvailable(), "کتاب باید غیرقابل دسترس باشد");

        // 10. بازگشت کتاب
        boolean bookReturned = libraryManager.returnBook(loanId);
        assertTrue(bookReturned, "بازگشت کتاب باید موفق باشد");

        // 11. بررسی وضعیت نهایی
        Book bookAfterReturn = libraryManager.findBookById("CYC001");
        assertTrue(bookAfterReturn.isAvailable(), "کتاب باید دوباره قابل دسترس باشد");

        Loan returnedLoan = libraryManager.getLoanById(loanId);
        assertNotNull(returnedLoan, "امانت باید پیدا شود");
        assertEquals("RETURNED", returnedLoan.getStatus(), "وضعیت امانت باید RETURNED باشد");
        assertNotNull(returnedLoan.getActualReturnDate(), "تاریخ بازگشت باید ثبت شده باشد");

        System.out.println("✅ چرخه کامل امانت با موفقیت انجام شد!");
    }

    @Test
    @DisplayName("Integration: ثبت‌نام، جستجو، درخواست امانت")
    void integrationTest_RegisterSearchRequest() {
        // 1. ثبت دانشجو
        libraryManager.registerStudent("integuser", "pass123", "S99999", "کاربر یکپارچه", "integ@university.ac.ir");

        // 2. اضافه کردن کتاب
        libraryManager.addBook(new Book("INT001", "کتاب تست یکپارچه", "نویسنده تست", "ناشر تست", 2024, "ISBN-INT-001"));

        // 3. ورود دانشجو
        libraryManager.studentLogin("integuser", "pass123");

        // 4. جستجوی کتاب
        List<Book> searchResults = libraryManager.searchBooks("یکپارچه", null, null);
        assertEquals(1, searchResults.size(), "باید کتاب اضافه شده پیدا شود");

        // 5. درخواست امانت
        boolean loanResult = libraryManager.requestLoan("INT001",
                LocalDate.now().plusDays(1),
                LocalDate.now().plusDays(14));

        assertTrue(loanResult, "درخواست امانت باید موفق باشد");

        // 6. بررسی
        assertEquals(1, libraryManager.getLoans().size(), "باید یک امانت ایجاد شده باشد");

        System.out.println("✅ تست یکپارچه با موفقیت انجام شد!");
    }
}
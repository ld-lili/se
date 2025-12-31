package ir.university.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.List;

import ir.university.library.model.*;
import ir.university.library.statistics.*;

class ReportServiceTest {

    private LibraryManager libraryManager;
    private Student testStudent;
    private Employee testEmployee;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager();
        libraryManager.clearAllData();

        // ایجاد داده‌های تست برای گزارش‌گیری
        createTestReportData();
    }

    private void createTestReportData() {
        // ایجاد دانشجو
        testStudent = new Student("teststudent", "pass123", "S10001", "دانشجوی تست", "test@university.ac.ir");
        libraryManager.getStudents().add(testStudent);

        // ایجاد کارمند
        testEmployee = new Employee("testemp", "pass123", "E001", "کارمند تست");
        libraryManager.getEmployees().add(testEmployee);

        // ایجاد کتاب‌ها
        Book book1 = new Book("B001", "کتاب ۱", "نویسنده ۱", "ناشر تست", 2023, "ISBN-001");
        Book book2 = new Book("B002", "کتاب ۲", "نویسنده ۲", "ناشر تست", 2023, "ISBN-002");

        libraryManager.getBooks().add(book1);
        libraryManager.getBooks().add(book2);

        // ایجاد امانت‌های تست

        // امانت بازگردانده شده به موقع
        Loan loan1 = new Loan("L001", testStudent.getUsername(), "B001",
                LocalDate.now().minusDays(30), LocalDate.now().minusDays(15));
        loan1.setStatus("RETURNED");
        loan1.setActualReturnDate(LocalDate.now().minusDays(14));
        loan1.setEmployeeUsername(testEmployee.getUsername());

        // امانت بازگردانده شده با تاخیر
        Loan loan2 = new Loan("L002", testStudent.getUsername(), "B002",
                LocalDate.now().minusDays(25), LocalDate.now().minusDays(10));
        loan2.setStatus("RETURNED");
        loan2.setActualReturnDate(LocalDate.now().minusDays(5)); // 5 روز تاخیر
        loan2.setEmployeeUsername(testEmployee.getUsername());

        libraryManager.getLoans().add(loan1);
        libraryManager.getLoans().add(loan2);
    }

    @Test
    @DisplayName("4-1: تولید گزارش برای یک دانشجو")
    void generateStudentReport() {
        // Act
        StudentStatistics stats = libraryManager.getStudentStatistics(testStudent.getUsername());

        // Assert
        assertNotNull(stats, "آمار دانشجو نباید null باشد");
        assertEquals(2, stats.getTotalLoans(), "تعداد کل امانت‌ها باید 2 باشد");
        assertEquals(0, stats.getNotReturned(), "تعداد کتاب‌های تحویل داده نشده باید 0 باشد");
        assertEquals(1, stats.getDelayedReturns(), "تعداد امانت‌های با تاخیر باید 1 باشد");
    }

    @Test
    @DisplayName("4-2: محاسبه آمار کلی کتابخانه")
    void calculateLibraryStatistics() {
        // Act
        LoanStatistics stats = libraryManager.getLoanStatistics();

        // Assert
        assertNotNull(stats, "آمار کتابخانه نباید null باشد");
        assertEquals(2, stats.getTotalRequests(), "تعداد کل درخواست‌ها باید 2 باشد");
        assertEquals(2, stats.getTotalApproved(), "تعداد کل امانت‌های تایید شده باید 2 باشد");

        // میانگین روزهای امانت
        // loan1: 16 روز (30-14)
        // loan2: 20 روز (25-5)
        // میانگین: (16+20)/2 = 18
        assertEquals(18.0, stats.getAverageLoanDays(), 0.01, "میانگین روزهای امانت باید 18 باشد");
    }

    @Test
    @DisplayName("4-3: آمار دانشجوی بدون امانت")
    void statisticsForStudentWithNoLoans() {
        // Arrange - ایجاد دانشجوی جدید بدون امانت
        Student newStudent = new Student("newstudent", "pass123", "S10003", "دانشجوی جدید", "new@university.ac.ir");
        libraryManager.getStudents().add(newStudent);

        // Act
        StudentStatistics stats = libraryManager.getStudentStatistics(newStudent.getUsername());

        // Assert
        assertNotNull(stats, "آمار دانشجوی جدید نباید null باشد");
        assertEquals(0, stats.getTotalLoans(), "دانشجوی جدید نباید امانتی داشته باشد");
        assertEquals(0, stats.getNotReturned(), "دانشجوی جدید نباید کتاب تحویل نداده داشته باشد");
        assertEquals(0, stats.getDelayedReturns(), "دانشجوی جدید نباید تاخیر داشته باشد");
    }

    @Test
    @DisplayName("4-4: آمار کتابخانه بدون هیچ امانتی")
    void libraryStatisticsWithNoLoans() {
        // Arrange - حذف تمام امانت‌ها
        libraryManager.getLoans().clear();

        // Act
        LoanStatistics stats = libraryManager.getLoanStatistics();

        // Assert
        assertNotNull(stats, "آمار کتابخانه نباید null باشد");
        assertEquals(0, stats.getTotalRequests(), "بدون امانت باید تعداد درخواست‌ها 0 باشد");
        assertEquals(0, stats.getTotalApproved(), "بدون امانت باید تعداد تایید شده‌ها 0 باشد");
        assertEquals(0.0, stats.getAverageLoanDays(), 0.01, "بدون امانت باید میانگین 0 باشد");
    }

    @Test
    @DisplayName("4-5: محاسبه آمار عملکرد کارمند")
    void calculateEmployeeStatistics() {
        // Act
        EmployeeStatistics stats = libraryManager.getEmployeeStatistics(testEmployee.getUsername());

        // Assert
        assertNotNull(stats, "آمار کارمند نباید null باشد");
        assertTrue(stats.getBooksRegistered() >= 0, "تعداد کتاب‌های ثبت شده باید معتبر باشد");
        assertEquals(2, stats.getLoansApproved(), "کارمند باید 2 امانت تایید کرده باشد");
        assertEquals(2, stats.getReturnsProcessed(), "کارمند باید 2 بازگشت ثبت کرده باشد");
    }

    @Test
    @DisplayName("4-6: مشاهده دانشجویان با بیشترین تاخیر")
    void viewTopDelayedStudents() {
        // Act
        List<Student> delayedStudents = libraryManager.getTopDelayedStudents();

        // Assert
        assertNotNull(delayedStudents, "لیست دانشجویان با تاخیر نباید null باشد");
        assertEquals(1, delayedStudents.size(), "باید یک دانشجو با تاخیر وجود داشته باشد");
        assertEquals(testStudent.getUsername(), delayedStudents.get(0).getUsername());
    }

    @Test
    @DisplayName("4-7: مشاهده امانت‌های معوقه")
    void viewOverdueLoans() {
        // Act
        List<Loan> overdueLoans = libraryManager.getOverdueLoans();

        // Assert
        assertNotNull(overdueLoans, "لیست امانت‌های معوقه نباید null باشد");
        // loan2 معوقه است
        assertEquals(1, overdueLoans.size(), "باید یک امانت معوقه وجود داشته باشد");
        assertEquals("L002", overdueLoans.get(0).getLoanId());
    }

    @Test
    @DisplayName("4-8: مشاهده تاریخچه امانت دانشجو")
    void viewStudentLoanHistory() {
        // Act
        List<Loan> studentLoans = libraryManager.getStudentLoanHistory(testStudent.getUsername());

        // Assert
        assertNotNull(studentLoans, "تاریخچه امانت دانشجو نباید null باشد");
        assertEquals(2, studentLoans.size(), "دانشجوی تست باید 2 امانت در تاریخچه داشته باشد");

        // بررسی وضعیت‌ها
        long returnedCount = studentLoans.stream()
                .filter(loan -> "RETURNED".equals(loan.getStatus()))
                .count();
        assertEquals(2, returnedCount, "هر دو امانت باید بازگردانده شده باشند");
    }
}
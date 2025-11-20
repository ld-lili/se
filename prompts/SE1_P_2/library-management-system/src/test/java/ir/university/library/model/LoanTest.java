package ir.university.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

@DisplayName("Loan Model Tests")
class LoanTest {

    @Test
    @DisplayName("Should create loan with correct properties")
    void testLoanCreation() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(14);
        Loan loan = new Loan("L001", "stu1", "B001", startDate, endDate);

        assertEquals("L001", loan.getLoanId());
        assertEquals("stu1", loan.getStudentUsername());
        assertEquals("B001", loan.getBookId());
        assertEquals(startDate, loan.getStartDate());
        assertEquals(endDate, loan.getEndDate());
        assertEquals("PENDING", loan.getStatus());
        assertNull(loan.getActualReturnDate());
        assertNull(loan.getEmployeeUsername());
    }

    @Test
    @DisplayName("Should update loan status and properties")
    void testLoanStatusChanges() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(14);
        Loan loan = new Loan("L001", "stu1", "B001", startDate, endDate);

        assertEquals("PENDING", loan.getStatus());

        loan.setStatus("APPROVED");
        loan.setEmployeeUsername("emp1");
        assertEquals("APPROVED", loan.getStatus());
        assertEquals("emp1", loan.getEmployeeUsername());

        LocalDate returnDate = LocalDate.now().plusDays(10);
        loan.setActualReturnDate(returnDate);
        loan.setStatus("RETURNED");
        assertEquals(returnDate, loan.getActualReturnDate());
        assertEquals("RETURNED", loan.getStatus());
    }

    @Test
    @DisplayName("Should detect overdue loan")
    void testOverdueCalculation() {
        LocalDate startDate = LocalDate.now().minusDays(20);
        LocalDate endDate = LocalDate.now().minusDays(5);
        Loan loan = new Loan("L001", "stu1", "B001", startDate, endDate);
        loan.setStatus("APPROVED");

        assertTrue(loan.isOverdue());
        assertTrue(loan.getOverdueDays() > 0);
    }

    @Test
    @DisplayName("Should not detect non-overdue loan")
    void testNotOverdueLoan() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(14);
        Loan loan = new Loan("L001", "stu1", "B001", startDate, endDate);
        loan.setStatus("APPROVED");

        assertFalse(loan.isOverdue());
        assertEquals(0, loan.getOverdueDays());
    }

    @Test
    @DisplayName("Should detect returned overdue loan")
    void testReturnedOverdueLoan() {
        LocalDate startDate = LocalDate.now().minusDays(30);
        LocalDate endDate = LocalDate.now().minusDays(15);
        LocalDate returnDate = LocalDate.now().minusDays(10);

        Loan loan = new Loan("L001", "stu1", "B001", startDate, endDate);
        loan.setStatus("RETURNED");
        loan.setActualReturnDate(returnDate);

        assertTrue(loan.isOverdue());
        assertEquals(5, loan.getOverdueDays());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        LocalDate startDate = LocalDate.now();
        LocalDate endDate = startDate.plusDays(14);
        Loan loan = new Loan("L001", "stu1", "B001", startDate, endDate);
        String result = loan.toString();

        assertTrue(result.contains("L001"));
        assertTrue(result.contains("stu1"));
        assertTrue(result.contains("B001"));
        assertTrue(result.contains("PENDING"));
    }
}
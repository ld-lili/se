package ir.university.library.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Loan Statistics Tests")
class LoanStatisticsTest {

    @Test
    @DisplayName("Should create statistics with correct values")
    void testLoanStatisticsCreation() {
        LoanStatistics stats = new LoanStatistics(100, 80, 14.5);

        assertEquals(100, stats.getTotalRequests());
        assertEquals(80, stats.getTotalApproved());
        assertEquals(14.5, stats.getAverageLoanDays(), 0.001);
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        LoanStatistics stats = new LoanStatistics(0, 0, 0.0);

        assertEquals(0, stats.getTotalRequests());
        assertEquals(0, stats.getTotalApproved());
        assertEquals(0.0, stats.getAverageLoanDays(), 0.001);
    }

    @Test
    @DisplayName("Should handle decimal average days")
    void testDecimalAverageDays() {
        LoanStatistics stats = new LoanStatistics(50, 40, 7.25);

        assertEquals(50, stats.getTotalRequests());
        assertEquals(40, stats.getTotalApproved());
        assertEquals(7.25, stats.getAverageLoanDays(), 0.001);
    }
}
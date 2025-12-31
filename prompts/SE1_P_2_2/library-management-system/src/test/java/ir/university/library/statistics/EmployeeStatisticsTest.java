package ir.university.library.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Employee Statistics Tests")
class EmployeeStatisticsTest {

    @Test
    @DisplayName("Should create statistics with correct values")
    void testEmployeeStatisticsCreation() {
        EmployeeStatistics stats = new EmployeeStatistics(50, 30, 25);

        assertEquals(50, stats.getBooksRegistered());
        assertEquals(30, stats.getLoansApproved());
        assertEquals(25, stats.getReturnsProcessed());
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        EmployeeStatistics stats = new EmployeeStatistics(0, 0, 0);

        assertEquals(0, stats.getBooksRegistered());
        assertEquals(0, stats.getLoansApproved());
        assertEquals(0, stats.getReturnsProcessed());
    }
}
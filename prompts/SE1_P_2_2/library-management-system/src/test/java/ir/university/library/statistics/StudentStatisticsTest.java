package ir.university.library.statistics;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student Statistics Tests")
class StudentStatisticsTest {

    @Test
    @DisplayName("Should create statistics with correct values")
    void testStudentStatisticsCreation() {
        StudentStatistics stats = new StudentStatistics(10, 2, 1);

        assertEquals(10, stats.getTotalLoans());
        assertEquals(2, stats.getNotReturned());
        assertEquals(1, stats.getDelayedReturns());
    }

    @Test
    @DisplayName("Should handle zero values")
    void testZeroValues() {
        StudentStatistics stats = new StudentStatistics(0, 0, 0);

        assertEquals(0, stats.getTotalLoans());
        assertEquals(0, stats.getNotReturned());
        assertEquals(0, stats.getDelayedReturns());
    }

    @Test
    @DisplayName("Should handle large values")
    void testLargeValues() {
        StudentStatistics stats = new StudentStatistics(1000, 500, 100);

        assertEquals(1000, stats.getTotalLoans());
        assertEquals(500, stats.getNotReturned());
        assertEquals(100, stats.getDelayedReturns());
    }
}
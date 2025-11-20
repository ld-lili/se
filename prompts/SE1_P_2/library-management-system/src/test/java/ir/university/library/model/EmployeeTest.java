package ir.university.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Employee Model Tests")
class EmployeeTest {

    @Test
    @DisplayName("Should create employee with correct properties")
    void testEmployeeCreation() {
        Employee employee = new Employee("emp1", "password123", "E001", "John Smith");

        assertEquals("emp1", employee.getUsername());
        assertEquals("E001", employee.getEmployeeId());
        assertEquals("John Smith", employee.getName());
        assertEquals("Employee", employee.getRole());
        assertTrue(employee.isActive());
    }

    @Test
    @DisplayName("Should update employee properties")
    void testEmployeeSetters() {
        Employee employee = new Employee("emp1", "password123", "E001", "John Smith");

        employee.setEmployeeId("E002");
        employee.setName("Jane Smith");
        employee.setActive(false);

        assertEquals("E002", employee.getEmployeeId());
        assertEquals("Jane Smith", employee.getName());
        assertFalse(employee.isActive());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        Employee employee = new Employee("emp1", "password123", "E001", "John Smith");
        String result = employee.toString();

        assertTrue(result.contains("John Smith"));
        assertTrue(result.contains("E001"));
        assertTrue(result.contains("emp1"));
    }
}
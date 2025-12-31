package ir.university.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Student Model Tests")
class StudentTest {

    @Test
    @DisplayName("Should create student with correct properties")
    void testStudentCreation() {
        Student student = new Student("stu1", "password123", "S001", "John Doe", "john@university.edu");

        assertEquals("stu1", student.getUsername());
        assertEquals("S001", student.getStudentId());
        assertEquals("John Doe", student.getName());
        assertEquals("john@university.edu", student.getEmail());
        assertEquals("Student", student.getRole());
        assertTrue(student.isActive());
    }

    @Test
    @DisplayName("Should update student properties")
    void testStudentSetters() {
        Student student = new Student("stu1", "password123", "S001", "John Doe", "john@university.edu");

        student.setStudentId("S002");
        student.setName("Jane Doe");
        student.setEmail("jane@university.edu");
        student.setActive(false);

        assertEquals("S002", student.getStudentId());
        assertEquals("Jane Doe", student.getName());
        assertEquals("jane@university.edu", student.getEmail());
        assertFalse(student.isActive());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        Student student = new Student("stu1", "password123", "S001", "John Doe", "john@university.edu");
        String result = student.toString();

        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("S001"));
        assertTrue(result.contains("stu1"));
        assertTrue(result.contains("john@university.edu"));
    }
}
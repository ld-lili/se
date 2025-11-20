package ir.university.library.model;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Book Model Tests")
class BookTest {

    @Test
    @DisplayName("Should create book with correct properties")
    void testBookCreation() {
        Book book = new Book("B001", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567890");

        assertEquals("B001", book.getBookId());
        assertEquals("Java Programming", book.getTitle());
        assertEquals("John Doe", book.getAuthor());
        assertEquals("Tech Pub", book.getPublisher());
        assertEquals(2023, book.getPublicationYear());
        assertEquals("1234567890", book.getIsbn());
        assertTrue(book.isAvailable());
    }

    @Test
    @DisplayName("Should change book availability")
    void testBookAvailability() {
        Book book = new Book("B001", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567890");

        assertTrue(book.isAvailable());
        book.setAvailable(false);
        assertFalse(book.isAvailable());
    }

    @Test
    @DisplayName("Should update book properties")
    void testBookSetters() {
        Book book = new Book("B001", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567890");

        book.setTitle("Advanced Java");
        book.setAuthor("Jane Smith");
        book.setPublisher("New Publisher");
        book.setPublicationYear(2024);
        book.setIsbn("0987654321");

        assertEquals("Advanced Java", book.getTitle());
        assertEquals("Jane Smith", book.getAuthor());
        assertEquals("New Publisher", book.getPublisher());
        assertEquals(2024, book.getPublicationYear());
        assertEquals("0987654321", book.getIsbn());
    }

    @Test
    @DisplayName("Should return correct string representation")
    void testToString() {
        Book book = new Book("B001", "Java Programming", "John Doe", "Tech Pub", 2023, "1234567890");
        String result = book.toString();

        assertTrue(result.contains("Java Programming"));
        assertTrue(result.contains("John Doe"));
        assertTrue(result.contains("B001"));
        assertTrue(result.contains("Tech Pub"));
    }
}
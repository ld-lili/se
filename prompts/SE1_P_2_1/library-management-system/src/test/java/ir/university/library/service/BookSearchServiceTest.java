package ir.university.library.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import ir.university.library.model.Book;

class BookSearchServiceTest {

    private LibraryManager libraryManager;

    @BeforeEach
    void setUp() {
        libraryManager = new LibraryManager();
        libraryManager.clearAllData();

        // اضافه کردن کتاب‌های تست
        addTestBooks();
    }

    private void addTestBooks() {
        // کتاب‌های تست با اطلاعات مختلف
        libraryManager.addBook(new Book("B001", "آموزش برنامه‌نویسی جاوا", "دکتر علیرضا محمدی", "نشر دانش", 2023,
                "978-600-00-0000-1"));
        libraryManager.addBook(
                new Book("B002", "پایگاه داده پیشرفته", "دکتر فاطمه احمدی", "نشر علم", 2022, "978-600-00-0000-2"));
        libraryManager.addBook(new Book("B003", "هوش مصنوعی و یادگیری ماشین", "دکتر محمد رضایی", "نشر پیشرو", 2024,
                "978-600-00-0000-3"));
        libraryManager.addBook(
                new Book("B004", "طراحی الگوریتم", "دکتر سارا کریمی", "نشر دانشگاهی", 2021, "978-600-00-0000-4"));
        libraryManager.addBook(
                new Book("B005", "شبکه‌های کامپیوتری", "دکتر احمد حسینی", "نشر فنی", 2023, "978-600-00-0000-5"));
        libraryManager.addBook(
                new Book("B006", "برنامه‌نویسی پیشرفته", "دکتر علیرضا محمدی", "نشر دانش", 2022, "978-600-00-0000-6"));
        libraryManager.addBook(
                new Book("B007", "مقدمه‌ای بر جاوا اسکریپت", "دکتر علی رحیمی", "نشر نوین", 2023, "978-600-00-0000-7"));
    }

    @Test
    @DisplayName("2-1: جستجو فقط با عنوان")
    void searchByTitleOnly() {
        // Arrange
        String title = "برنامه‌نویسی";

        // Act
        List<Book> result = libraryManager.searchBooks(title, null, null);

        // Assert
        assertEquals(2, result.size(), "باید دو کتاب با عنوان 'برنامه‌نویسی' پیدا شود");

        // بررسی عناوین
        List<String> titles = result.stream()
                .map(Book::getTitle)
                .toList();

        assertTrue(titles.contains("آموزش برنامه‌نویسی جاوا"));
        assertTrue(titles.contains("برنامه‌نویسی پیشرفته"));

        // چاپ اطلاعات برای دیباگ
        System.out.println("نتایج جستجوی '" + title + "':");
        result.forEach(book -> System.out.println("  - " + book.getTitle()));
    }

    @Test
    @DisplayName("2-2: جستجو با ترکیب نویسنده و سال انتشار")
    void searchByAuthorAndYear() {
        // Arrange
        String author = "محمدی";
        Integer year = 2023;

        // Act
        List<Book> result = libraryManager.searchBooks(null, author, year);

        // Assert
        assertEquals(1, result.size(), "باید یک کتاب از نویسنده 'محمدی' در سال 2023 پیدا شود");

        Book foundBook = result.get(0);
        assertEquals("آموزش برنامه‌نویسی جاوا", foundBook.getTitle());
        assertTrue(foundBook.getAuthor().contains("محمدی"));
        assertEquals(2023, foundBook.getPublicationYear());
    }

    @Test
    @DisplayName("2-3: جستجو بدون هیچ معیاری (همه پارامترها null)")
    void searchWithNoCriteria() {
        // Act
        List<Book> result = libraryManager.searchBooks(null, null, null);

        // Assert
        assertEquals(7, result.size(), "باید تمام کتاب‌ها برگردانده شوند");
    }

    @Test
    @DisplayName("2-4: جستجویی که هیچ کتابی مطابقت ندارد")
    void searchWithNoMatches() {
        // Arrange
        String title = "کتابی که وجود ندارد";
        String author = "نویسنده ناشناس";
        Integer year = 1990;

        // Act
        List<Book> result = libraryManager.searchBooks(title, author, year);

        // Assert
        assertTrue(result.isEmpty(), "هیچ کتابی نباید پیدا شود");
    }

    @Test
    @DisplayName("2-5: جستجو با عنوان جزئی (case insensitive)")
    void searchWithPartialTitleCaseInsensitive() {
        // Arrange
        // تست با حروف فارسی (اصل منطق case insensitive روی فارسی هم باید کار کند)
        String title = "جاوا";

        // Act
        List<Book> result = libraryManager.searchBooks(title, null, null);

        // Assert
        assertEquals(1, result.size(), "باید کتاب جاوا پیدا شود");
        assertTrue(result.get(0).getTitle().contains("جاوا"));

        // تست جستجوی جزئی دیگر
        String title2 = "شبکه";
        List<Book> result2 = libraryManager.searchBooks(title2, null, null);
        assertEquals(1, result2.size(), "باید کتاب شبکه پیدا شود");
        assertTrue(result2.get(0).getTitle().contains("شبکه"));
    }

    @Test
    @DisplayName("جستجو با عنوان خالی")
    void searchWithEmptyTitle() {
        // Arrange
        String title = "";

        // Act
        List<Book> result = libraryManager.searchBooks(title, null, null);

        // Assert
        assertEquals(7, result.size(), "عنوان خالی باید تمام کتاب‌ها را برگرداند");
    }

    @Test
    @DisplayName("جستجو با سال انتشار خاص")
    void searchBySpecificYear() {
        // Arrange
        Integer year = 2023;

        // Act
        List<Book> result = libraryManager.searchBooks(null, null, year);

        // Assert
        // کتاب‌های سال 2023: آموزش برنامه‌نویسی جاوا، شبکه‌های کامپیوتری، مقدمه‌ای بر
        // جاوا اسکریپت
        assertEquals(3, result.size(), "باید سه کتاب از سال 2023 پیدا شود");
        assertTrue(result.stream().allMatch(book -> book.getPublicationYear() == 2023));
    }

    @Test
    @DisplayName("جستجوی ترکیبی عنوان و نویسنده")
    void searchByTitleAndAuthor() {
        // Arrange
        String title = "جاوا";
        String author = "محمدی";

        // Act
        List<Book> result = libraryManager.searchBooks(title, author, null);

        // Assert
        assertEquals(1, result.size(), "باید کتاب جاوا از نویسنده محمدی پیدا شود");
        assertEquals("آموزش برنامه‌نویسی جاوا", result.get(0).getTitle());
        assertTrue(result.get(0).getAuthor().contains("محمدی"));
    }

    @Test
    @DisplayName("جستجو با جستجوی جزئی در نویسنده")
    void searchByPartialAuthor() {
        // Arrange
        String author = "احمد";

        // Act
        List<Book> result = libraryManager.searchBooks(null, author, null);

        // Assert
        assertEquals(1, result.size(), "باید کتاب نویسنده احمد پیدا شود");
        assertTrue(result.get(0).getAuthor().contains("احمد"));
    }
}
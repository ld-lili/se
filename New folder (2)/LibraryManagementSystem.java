import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class LibraryManagementSystem {
    private LibraryManager libraryManager;
    private Scanner scanner;

    public LibraryManagementSystem() {
        this.libraryManager = new LibraryManager();
        this.scanner = new Scanner(System.in);
    }

    public void run() {
        showWelcomeMessage();

        while (true) {
            System.out.println("\n" + "=".repeat(50));
            System.out.println("       سیستم مدیریت کتابخانه دانشگاه");
            System.out.println("=".repeat(50));
            System.out.println("1. 📚 دانشجو");
            System.out.println("2. 👥 کاربر مهمان");
            System.out.println("3. 👨‍💼 کارمند کتابخانه");
            System.out.println("4. 👑 مدیر سیستم");
            System.out.println("5. 📖 راهنمای ورود");
            System.out.println("0. 🚪 خروج");
            System.out.print("لطفا نقش خود را انتخاب کنید: ");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    studentMenu();
                    break;
                case 2:
                    guestMenu();
                    break;
                case 3:
                    employeeLogin();
                    break;
                case 4:
                    managerLogin();
                    break;
                case 5:
                    showLoginGuide();
                    break;
                case 0:
                    exitSystem();
                    return;
                default:
                    System.out.println("❌ گزینه نامعتبر!");
            }
        }
    }

    private void showWelcomeMessage() {
        System.out.println("✨".repeat(60));
        System.out.println("              به سیستم مدیریت کتابخانه دانشگاه خوش آمدید!");
        System.out.println("✨".repeat(60));
        System.out.println("🔰 برای اولین استفاده از اطلاعات پیش‌فرض زیر استفاده کنید:");
        System.out.println("   👑 مدیر سیستم:    manager / manager123");
        System.out.println("   👨‍💼 کارمندان:       emp1, emp2, emp3 / 1234");
        System.out.println("   📚 دانشجویان:      stu1, stu2, stu3 / 1234");
        System.out.println("📖 برای مشاهده راهنمای کامل، گزینه 5 را انتخاب کنید.");
        System.out.println("✨".repeat(60));
    }

    private void showLoginGuide() {
        System.out.println("\n📖 === راهنمای ورود به سیستم ===");

        System.out.println("\n👑 مدیر سیستم:");
        System.out.println("   📧 نام کاربری: manager");
        System.out.println("   🔐 رمز عبور: manager123");
        System.out.println("   ✅ دسترسی: مدیریت کامل سیستم، تعریف کارمند، مشاهده گزارشات پیشرفته");

        System.out.println("\n👨‍💼 کارمندان کتابخانه:");
        System.out.println("   📧 کارمند ۱: emp1 / 1234");
        System.out.println("   📧 کارمند ۲: emp2 / 1234");
        System.out.println("   📧 کارمند ۳: emp3 / 1234");
        System.out.println("   ✅ دسترسی: ثبت کتاب، مدیریت امانت، مدیریت دانشجویان");

        System.out.println("\n📚 دانشجویان:");
        System.out.println("   📧 دانشجو ۱: stu1 / 1234");
        System.out.println("   📧 دانشجو ۲: stu2 / 1234");
        System.out.println("   📧 دانشجو ۳: stu3 / 1234");
        System.out.println("   ✅ دسترسی: جستجوی کتاب، درخواست امانت، مشاهده تاریخچه");
        System.out.println("   💡 نکته: دانشجویان جدید باید ابتدا ثبت نام کنند");

        System.out.println("\n👥 کاربران مهمان:");
        System.out.println("   ✅ دسترسی: مشاهده آمار، جستجوی محدود کتاب‌ها");
        System.out.println("   💡 نکته: نیاز به ورود ندارند");
    }

    private void exitSystem() {
        System.out.println("\n✨".repeat(50));
        System.out.println("   با تشکر از استفاده شما از سیستم کتابخانه");
        System.out.println("           تمام داده‌ها ذخیره شدند.");
        System.out.println("✨".repeat(50));
        libraryManager.logout();
    }

    // Student Menu Methods
    private void studentMenu() {
        System.out.println("\n📚 === منوی دانشجو ===");
        System.out.println("1. 📝 ثبت نام دانشجوی جدید");
        System.out.println("2. 🔐 ورود دانشجو");
        System.out.println("0. ↩️ بازگشت");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                registerStudent();
                break;
            case 2:
                loginStudent();
                break;
            case 0:
                return;
            default:
                System.out.println("❌ گزینه نامعتبر!");
        }
    }

    private void registerStudent() {
        System.out.println("\n📝 --- ثبت نام دانشجوی جدید ---");
        System.out.print("📧 نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("🔐 رمز عبور: ");
        String password = scanner.nextLine();
        System.out.print("🎓 شماره دانشجویی: ");
        String studentId = scanner.nextLine();
        System.out.print("👤 نام کامل: ");
        String name = scanner.nextLine();
        System.out.print("📧 ایمیل: ");
        String email = scanner.nextLine();

        if (libraryManager.registerStudent(username, password, studentId, name, email)) {
            System.out.println("✅ ثبت نام با موفقیت انجام شد!");
            System.out.println("💡 اکنون می‌توانید با نام کاربری و رمز عبور خود وارد شوید.");
        } else {
            System.out.println("❌ خطا در ثبت نام! نام کاربری یا شماره دانشجویی تکراری است.");
        }
    }

    private void loginStudent() {
        System.out.println("\n🔐 --- ورود دانشجو ---");
        System.out.print("📧 نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("🔐 رمز عبور: ");
        String password = scanner.nextLine();

        if (libraryManager.studentLogin(username, password)) {
            System.out.println("✅ ورود موفق! خوش آمدید " + username);
            loggedInStudentMenu();
        } else {
            System.out.println("❌ ورود ناموفق! نام کاربری یا رمز عبور اشتباه است.");
        }
    }

    private void loggedInStudentMenu() {
        while (true) {
            System.out.println("\n🎓 === منوی دانشجوی وارد شده ===");
            System.out.println("👤 کاربر: " + libraryManager.getCurrentUser().getUsername());
            System.out.println("1. 🔍 جستجوی کتاب");
            System.out.println("2. 📖 درخواست امانت کتاب");
            System.out.println("3. 📋 مشاهده تاریخچه امانت‌های من");
            System.out.println("4. 👀 مشاهده کتاب‌های موجود");
            System.out.println("0. 🚪 خروج");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    searchBooksStudent();
                    break;
                case 2:
                    requestLoan();
                    break;
                case 3:
                    viewMyLoanHistory();
                    break;
                case 4:
                    showAvailableBooksStudent();
                    break;
                case 0:
                    libraryManager.logout();
                    return;
                default:
                    System.out.println("❌ گزینه نامعتبر!");
            }
        }
    }

    private void searchBooksStudent() {
        System.out.println("\n🔍 --- جستجوی کتاب ---");
        System.out.print("📖 عنوان کتاب (اختیاری): ");
        String title = scanner.nextLine();
        System.out.print("👤 نام نویسنده (اختیاری): ");
        String author = scanner.nextLine();
        System.out.print("📅 سال نشر (اختیاری): ");
        Integer year = getIntInputOrNull();

        List<Book> books = libraryManager.searchBooks(
                title.isEmpty() ? null : title,
                author.isEmpty() ? null : author,
                year);

        System.out.println("\n📚 نتایج جستجو:");
        if (books.isEmpty()) {
            System.out.println("❌ کتابی یافت نشد.");
        } else {
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                String status = book.isAvailable() ? "🟢 موجود" : "🔴 امانت داده شده";
                System.out.println((i + 1) + ". " + book.getTitle() + " - " + book.getAuthor() +
                        " (" + book.getPublicationYear() + ") - " + status);
            }
        }
    }

    private void requestLoan() {
        System.out.println("\n📖 --- درخواست امانت کتاب ---");

        // نمایش کتاب‌های موجود
        List<Book> availableBooks = libraryManager.searchBooks(null, null, null)
                .stream()
                .filter(Book::isAvailable)
                .toList();

        if (availableBooks.isEmpty()) {
            System.out.println("❌ هیچ کتابی برای امانت موجود نیست.");
            return;
        }

        System.out.println("📚 کتاب‌های موجود برای امانت:");
        for (int i = 0; i < availableBooks.size(); i++) {
            Book book = availableBooks.get(i);
            System.out.println((i + 1) + ". " + book.getTitle() + " - " + book.getAuthor() +
                    " (ID: " + book.getBookId() + ")");
        }

        System.out.print("\n🔢 شماره کتاب برای امانت (0 برای بازگشت): ");
        int bookChoice = getIntInput();
        if (bookChoice == 0 || bookChoice > availableBooks.size()) {
            return;
        }

        Book selectedBook = availableBooks.get(bookChoice - 1);

        System.out.print("📅 تاریخ شروع (YYYY-MM-DD): ");
        LocalDate startDate = getDateInput();
        System.out.print("📅 تاریخ پایان (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput();

        if (libraryManager.requestLoan(selectedBook.getBookId(), startDate, endDate)) {
            System.out.println("✅ درخواست امانت با موفقیت ثبت شد!");
            System.out.println("💡 منتظر تایید کارمند کتابخانه باشید.");
        } else {
            System.out.println("❌ خطا در ثبت درخواست امانت!");
        }
    }

    private void viewMyLoanHistory() {
        String username = libraryManager.getCurrentUser().getUsername();
        List<Loan> myLoans = libraryManager.getStudentLoanHistory(username);
        StudentStatistics stats = libraryManager.getStudentStatistics(username);

        System.out.println("\n📋 --- تاریخچه امانت‌های من ---");
        System.out.println("📊 آمار شما:");
        System.out.println("   📖 تعداد کل امانت‌ها: " + stats.getTotalLoans());
        System.out.println("   ⏳ کتاب‌های تحویل داده نشده: " + stats.getNotReturned());
        System.out.println("   ⚠️  امانت‌های با تاخیر: " + stats.getDelayedReturns());

        System.out.println("\n📜 تاریخچه امانت‌ها:");
        if (myLoans.isEmpty()) {
            System.out.println("ℹ️  هیچ امانتی یافت نشد.");
        } else {
            for (Loan loan : myLoans) {
                String statusIcon = "🟡";
                if ("APPROVED".equals(loan.getStatus()))
                    statusIcon = "🟢";
                else if ("RETURNED".equals(loan.getStatus()))
                    statusIcon = "🔵";
                else if (loan.isOverdue())
                    statusIcon = "🔴";

                Book book = libraryManager.findBookById(loan.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "نامشخص";

                System.out.println(statusIcon + " " + bookTitle +
                        " - وضعیت: " + getStatusText(loan.getStatus()) +
                        " - شروع: " + loan.getStartDate() +
                        " - پایان: " + loan.getEndDate() +
                        (loan.getActualReturnDate() != null ? " - بازگشت: " + loan.getActualReturnDate() : ""));
            }
        }
    }

    private void showAvailableBooksStudent() {
        System.out.println("\n📚 --- کتاب‌های موجود در کتابخانه ---");
        List<Book> books = libraryManager.getBooks();
        List<Book> availableBooks = books.stream()
                .filter(Book::isAvailable)
                .toList();

        if (availableBooks.isEmpty()) {
            System.out.println("❌ هیچ کتابی در حال حاضر موجود نیست.");
            return;
        }

        for (int i = 0; i < availableBooks.size(); i++) {
            Book book = availableBooks.get(i);
            System.out.println((i + 1) + ". 📖 " + book.getTitle() +
                    " - 👤 " + book.getAuthor() +
                    " - 📅 " + book.getPublicationYear() +
                    " - 🆔 " + book.getBookId());
        }
        System.out.println("📊 تعداد کتاب‌های موجود: " + availableBooks.size() + " از " + books.size());
    }

    // Guest Menu Methods
    private void guestMenu() {
        while (true) {
            System.out.println("\n👥 === منوی کاربر مهمان ===");
            System.out.println("1. 👥 مشاهده تعداد دانشجویان");
            System.out.println("2. 🔍 جستجوی کتاب بر اساس نام");
            System.out.println("3. 📊 مشاهده آمار کلی");
            System.out.println("4. 📚 مشاهده کتاب‌های موجود");
            System.out.println("0. ↩️ بازگشت");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    System.out.println("🎓 تعداد دانشجویان ثبت‌نام شده: " + libraryManager.getTotalStudents());
                    break;
                case 2:
                    searchBooksGuest();
                    break;
                case 3:
                    showGuestStatistics();
                    break;
                case 4:
                    showAvailableBooksGuest();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("❌ گزینه نامعتبر!");
            }
        }
    }

    private void searchBooksGuest() {
        System.out.print("🔍 نام کتاب: ");
        String title = scanner.nextLine();

        List<Book> books = libraryManager.searchBooksByTitle(title);
        System.out.println("\n📚 نتایج جستجو:");
        if (books.isEmpty()) {
            System.out.println("❌ کتابی یافت نشد.");
        } else {
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                System.out.println((i + 1) + ". 📖 " + book.getTitle() +
                        " - 👤 " + book.getAuthor() +
                        " - 🏢 " + book.getPublisher() +
                        " - 📅 " + book.getPublicationYear());
            }
        }
    }

    private void showGuestStatistics() {
        System.out.println("\n📊 --- آمار کلی کتابخانه ---");
        System.out.println("🎓 تعداد کل دانشجویان: " + libraryManager.getTotalStudents());
        System.out.println("📚 تعداد کل کتاب‌ها: " + libraryManager.getTotalBooks());
        System.out.println("📖 تعداد کل امانت‌ها: " + libraryManager.getTotalLoans());
        System.out.println("🔴 کتاب‌های امانت داده شده: " + libraryManager.getCurrentLoans());
        System.out.println("🟢 کتاب‌های موجود: " + libraryManager.getAvailableBooks());

        List<Loan> pendingLoans = libraryManager.getPendingLoans();
        System.out.println("🟡 درخواست‌های در انتظار تایید: " + pendingLoans.size());
    }

    private void showAvailableBooksGuest() {
        System.out.println("\n📚 --- کتاب‌های موجود در کتابخانه ---");
        List<Book> books = libraryManager.getBooks();
        List<Book> availableBooks = books.stream()
                .filter(Book::isAvailable)
                .toList();

        if (availableBooks.isEmpty()) {
            System.out.println("❌ هیچ کتابی در حال حاضر موجود نیست.");
            return;
        }

        for (int i = 0; i < availableBooks.size(); i++) {
            Book book = availableBooks.get(i);
            System.out.println((i + 1) + ". 📖 " + book.getTitle() +
                    " - 👤 " + book.getAuthor() +
                    " - 🏢 " + book.getPublisher() +
                    " - 📅 " + book.getPublicationYear());
        }
        System.out.println("📊 تعداد کتاب‌های موجود: " + availableBooks.size() + " از " + books.size());
    }

    // Employee Menu Methods
    private void employeeLogin() {
        System.out.println("\n👨‍💼 --- ورود کارمند کتابخانه ---");
        System.out.println("💡 کارمندان پیش‌فرض: emp1, emp2, emp3 / 1234");

        System.out.print("📧 نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("🔐 رمز عبور: ");
        String password = scanner.nextLine();

        if (libraryManager.employeeLogin(username, password)) {
            System.out.println("✅ ورود موفق! خوش آمدید " + username);
            loggedInEmployeeMenu();
        } else {
            System.out.println("❌ ورود ناموفق! نام کاربری یا رمز عبور اشتباه است.");
            System.out.println("💡 اگر کارمند جدید هستید، باید توسط مدیر سیستم تعریف شوید.");
        }
    }

    private void loggedInEmployeeMenu() {
        while (true) {
            System.out.println("\n👨‍💼 === منوی کارمند کتابخانه ===");
            System.out.println("👤 کاربر: " + libraryManager.getCurrentUser().getUsername());
            System.out.println("1. 🔐 تغییر رمز عبور");
            System.out.println("2. 📚 ثبت کتاب جدید");
            System.out.println("3. 🔍 جستجو و ویرایش کتاب");
            System.out.println("4. 📋 بررسی درخواست‌های امانت");
            System.out.println("5. 👨‍🎓 مشاهده تاریخچه امانت دانشجو");
            System.out.println("6. ⚙️ فعال/غیرفعال کردن دانشجو");
            System.out.println("7. 📥 ثبت بازگشت کتاب");
            System.out.println("8. 📖 مشاهده کتاب‌های موجود");
            System.out.println("9. 📊 مشاهده آمار سریع");
            System.out.println("10. 🚨 مشاهده امانت‌های معوقه");
            System.out.println("0. 🚪 خروج");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    changePassword();
                    break;
                case 2:
                    addBook();
                    break;
                case 3:
                    searchAndEditBook();
                    break;
                case 4:
                    reviewLoanRequests();
                    break;
                case 5:
                    viewStudentLoanHistory();
                    break;
                case 6:
                    toggleStudentStatus();
                    break;
                case 7:
                    returnBook();
                    break;
                case 8:
                    showAvailableBooksEmployee();
                    break;
                case 9:
                    showQuickStats();
                    break;
                case 10:
                    showOverdueLoans();
                    break;
                case 0:
                    libraryManager.logout();
                    return;
                default:
                    System.out.println("❌ گزینه نامعتبر!");
            }
        }
    }

    private void changePassword() {
        System.out.print("🔐 رمز عبور جدید: ");
        String newPassword = scanner.nextLine();
        libraryManager.getCurrentUser().setPassword(newPassword);
        System.out.println("✅ رمز عبور با موفقیت تغییر یافت!");
    }

    private void addBook() {
        System.out.println("\n📚 --- ثبت کتاب جدید ---");
        System.out.print("🆔 شناسه کتاب: ");
        String bookId = scanner.nextLine();
        System.out.print("📖 عنوان: ");
        String title = scanner.nextLine();
        System.out.print("👤 نویسنده: ");
        String author = scanner.nextLine();
        System.out.print("🏢 ناشر: ");
        String publisher = scanner.nextLine();
        System.out.print("📅 سال نشر: ");
        int year = getIntInput();
        System.out.print("🏷️ شابک: ");
        String isbn = scanner.nextLine();

        Book book = new Book(bookId, title, author, publisher, year, isbn);
        if (libraryManager.addBook(book)) {
            System.out.println("✅ کتاب با موفقیت ثبت شد!");
        } else {
            System.out.println("❌ خطا در ثبت کتاب! شناسه یا شابک تکراری است.");
        }
    }

    private void searchAndEditBook() {
        System.out.println("\n🔍 --- جستجو و ویرایش کتاب ---");
        System.out.print("📖 عنوان کتاب برای جستجو: ");
        String title = scanner.nextLine();

        List<Book> books = libraryManager.searchBooksByTitle(title);
        if (books.isEmpty()) {
            System.out.println("❌ کتابی یافت نشد.");
            return;
        }

        System.out.println("\n📚 کتاب‌های یافت شده:");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String status = book.isAvailable() ? "🟢 موجود" : "🔴 امانت داده شده";
            System.out.println((i + 1) + ". " + book.getTitle() + " - " + book.getAuthor() +
                    " (" + book.getPublicationYear() + ") - " + status + " - ID: " + book.getBookId());
        }

        System.out.print("🔢 شماره کتاب برای ویرایش (0 برای بازگشت): ");
        int choice = getIntInput();
        if (choice == 0 || choice > books.size()) {
            return;
        }

        Book selectedBook = books.get(choice - 1);
        editBook(selectedBook.getBookId());
    }

    private void editBook(String bookId) {
        System.out.println("\n✏️ --- ویرایش کتاب ---");
        Book currentBook = libraryManager.findBookById(bookId);
        if (currentBook == null) {
            System.out.println("❌ کتاب یافت نشد!");
            return;
        }

        System.out.println("📋 اطلاعات فعلی:");
        System.out.println("عنوان: " + currentBook.getTitle());
        System.out.println("نویسنده: " + currentBook.getAuthor());
        System.out.println("ناشر: " + currentBook.getPublisher());
        System.out.println("سال نشر: " + currentBook.getPublicationYear());
        System.out.println("شابک: " + currentBook.getIsbn());

        System.out.println("\n📝 اطلاعات جدید را وارد کنید (در صورت عدم تغییر Enter بزنید):");

        System.out.print("📖 عنوان جدید: ");
        String title = scanner.nextLine();
        System.out.print("👤 نویسنده جدید: ");
        String author = scanner.nextLine();
        System.out.print("🏢 ناشر جدید: ");
        String publisher = scanner.nextLine();
        System.out.print("📅 سال نشر جدید: ");
        String yearStr = scanner.nextLine();
        System.out.print("🏷️ شابک جدید: ");
        String isbn = scanner.nextLine();

        // استفاده از مقادیر فعلی اگر کاربر چیزی وارد نکرده
        title = title.isEmpty() ? currentBook.getTitle() : title;
        author = author.isEmpty() ? currentBook.getAuthor() : author;
        publisher = publisher.isEmpty() ? currentBook.getPublisher() : publisher;
        int year = yearStr.isEmpty() ? currentBook.getPublicationYear() : Integer.parseInt(yearStr);
        isbn = isbn.isEmpty() ? currentBook.getIsbn() : isbn;

        Book updatedBook = new Book(bookId, title, author, publisher, year, isbn);
        if (libraryManager.updateBook(bookId, updatedBook)) {
            System.out.println("✅ کتاب با موفقیت ویرایش شد!");
        } else {
            System.out.println("❌ خطا در ویرایش کتاب!");
        }
    }

    private void reviewLoanRequests() {
        System.out.println("\n📋 --- بررسی درخواست‌های امانت ---");
        List<Loan> pendingLoans = libraryManager.getPendingLoans();

        if (pendingLoans.isEmpty()) {
            System.out.println("✅ هیچ درخواست امانت در حال انتظار وجود ندارد.");
            return;
        }

        System.out.println("🟡 درخواست‌های در حال انتظار:");
        for (int i = 0; i < pendingLoans.size(); i++) {
            Loan loan = pendingLoans.get(i);
            Book book = libraryManager.findBookById(loan.getBookId());
            Student student = libraryManager.findStudentByUsername(loan.getStudentUsername());
            String bookTitle = (book != null) ? book.getTitle() : "نامشخص";
            String studentName = (student != null) ? student.getName() : loan.getStudentUsername();

            System.out.println((i + 1) + ". 👨‍🎓 دانشجو: " + studentName +
                    " - 📖 کتاب: " + bookTitle +
                    " - 📅 تاریخ شروع: " + loan.getStartDate());
        }

        System.out.print("🔢 شماره درخواست برای تایید (0 برای بازگشت): ");
        int choice = getIntInput();
        if (choice == 0 || choice > pendingLoans.size()) {
            return;
        }

        Loan selectedLoan = pendingLoans.get(choice - 1);
        if (libraryManager.approveLoan(selectedLoan.getLoanId(), libraryManager.getCurrentUser().getUsername())) {
            System.out.println("✅ درخواست امانت با موفقیت تایید شد!");
        } else {
            System.out.println("❌ خطا در تایید درخواست امانت!");
        }
    }

    private void viewStudentLoanHistory() {
        System.out.println("\n👨‍🎓 --- مشاهده تاریخچه امانت دانشجو ---");
        System.out.print("📧 نام کاربری دانشجو: ");
        String username = scanner.nextLine();

        Student student = libraryManager.findStudentByUsername(username);
        if (student == null) {
            System.out.println("❌ دانشجو با این نام کاربری یافت نشد!");
            return;
        }

        List<Loan> studentLoans = libraryManager.getStudentLoanHistory(username);
        StudentStatistics stats = libraryManager.getStudentStatistics(username);

        System.out.println("\n📊 آمار دانشجو:");
        System.out.println("👤 نام: " + student.getName());
        System.out.println("🎓 شماره دانشجویی: " + student.getStudentId());
        System.out.println("📧 ایمیل: " + student.getEmail());
        System.out.println("📖 تعداد کل امانت‌ها: " + stats.getTotalLoans());
        System.out.println("⏳ کتاب‌های تحویل داده نشده: " + stats.getNotReturned());
        System.out.println("⚠️ امانت‌های با تاخیر: " + stats.getDelayedReturns());
        System.out.println("🔘 وضعیت: " + (student.isActive() ? "🟢 فعال" : "🔴 غیرفعال"));

        System.out.println("\n📜 تاریخچه امانت‌ها:");
        if (studentLoans.isEmpty()) {
            System.out.println("ℹ️ هیچ امانتی یافت نشد.");
        } else {
            for (Loan loan : studentLoans) {
                Book book = libraryManager.findBookById(loan.getBookId());
                String bookTitle = (book != null) ? book.getTitle() : "نامشخص";
                String statusIcon = "🟡";
                if ("APPROVED".equals(loan.getStatus()))
                    statusIcon = "🟢";
                else if ("RETURNED".equals(loan.getStatus()))
                    statusIcon = "🔵";
                else if (loan.isOverdue())
                    statusIcon = "🔴";

                System.out.println(statusIcon + " " + bookTitle +
                        " - وضعیت: " + getStatusText(loan.getStatus()) +
                        " - شروع: " + loan.getStartDate() +
                        " - پایان: " + loan.getEndDate() +
                        (loan.getActualReturnDate() != null ? " - بازگشت: " + loan.getActualReturnDate() : ""));
            }
        }
    }

    private void toggleStudentStatus() {
        System.out.println("\n⚙️ --- فعال/غیرفعال کردن دانشجو ---");
        System.out.print("📧 نام کاربری دانشجو: ");
        String username = scanner.nextLine();

        Student student = libraryManager.findStudentByUsername(username);
        if (student == null) {
            System.out.println("❌ دانشجو با این نام کاربری یافت نشد!");
            return;
        }

        String currentStatus = student.isActive() ? "فعال" : "غیرفعال";
        String newStatus = student.isActive() ? "غیرفعال" : "فعال";

        System.out.println("👤 دانشجو: " + student.getName());
        System.out.println("🔘 وضعیت فعلی: " + currentStatus);
        System.out.print("آیا می‌خواهید وضعیت را به " + newStatus + " تغییر دهید؟ (y/n): ");
        String confirm = scanner.nextLine();

        if (confirm.equalsIgnoreCase("y")) {
            if (libraryManager.toggleStudentStatus(username)) {
                System.out.println("✅ وضعیت دانشجو با موفقیت تغییر یافت!");
            } else {
                System.out.println("❌ خطا در تغییر وضعیت دانشجو!");
            }
        } else {
            System.out.println("ℹ️ تغییر وضعیت لغو شد.");
        }
    }

    private void returnBook() {
        System.out.println("\n📥 --- ثبت بازگشت کتاب ---");

        // نمایش امانت‌های فعال
        List<Loan> activeLoans = libraryManager.getLoans().stream()
                .filter(loan -> "APPROVED".equals(loan.getStatus()))
                .toList();

        if (activeLoans.isEmpty()) {
            System.out.println("✅ هیچ امانت فعالی وجود ندارد.");
            return;
        }

        System.out.println("📋 امانت‌های فعال:");
        for (int i = 0; i < activeLoans.size(); i++) {
            Loan loan = activeLoans.get(i);
            Book book = libraryManager.findBookById(loan.getBookId());
            Student student = libraryManager.findStudentByUsername(loan.getStudentUsername());
            String bookTitle = (book != null) ? book.getTitle() : "نامشخص";
            String studentName = (student != null) ? student.getName() : loan.getStudentUsername();

            System.out.println((i + 1) + ". 👨‍🎓 " + studentName +
                    " - 📖 " + bookTitle +
                    " - 🆔 " + loan.getLoanId());
        }

        System.out.print("🔢 شماره امانت برای بازگشت (0 برای بازگشت): ");
        int choice = getIntInput();
        if (choice == 0 || choice > activeLoans.size()) {
            return;
        }

        Loan selectedLoan = activeLoans.get(choice - 1);
        if (libraryManager.returnBook(selectedLoan.getLoanId())) {
            System.out.println("✅ بازگشت کتاب با موفقیت ثبت شد!");
        } else {
            System.out.println("❌ خطا در ثبت بازگشت کتاب!");
        }
    }

    private void showAvailableBooksEmployee() {
        System.out.println("\n📚 --- کتاب‌های موجود در کتابخانه ---");
        List<Book> books = libraryManager.getBooks();
        List<Book> availableBooks = books.stream()
                .filter(Book::isAvailable)
                .toList();

        if (availableBooks.isEmpty()) {
            System.out.println("❌ هیچ کتابی در حال حاضر موجود نیست.");
            return;
        }

        for (int i = 0; i < availableBooks.size(); i++) {
            Book book = availableBooks.get(i);
            System.out.println((i + 1) + ". 📖 " + book.getTitle() +
                    " - 👤 " + book.getAuthor() +
                    " - 🏢 " + book.getPublisher() +
                    " - 📅 " + book.getPublicationYear() +
                    " - 🆔 " + book.getBookId());
        }
        System.out.println("📊 تعداد کتاب‌های موجود: " + availableBooks.size() + " از " + books.size());
    }

    private void showQuickStats() {
        System.out.println("\n📊 --- آمار سریع کتابخانه ---");
        System.out.println("🎓 تعداد کل دانشجویان: " + libraryManager.getTotalStudents());
        System.out.println("🎓 دانشجویان فعال: " + libraryManager.getActiveStudents());
        System.out.println("📚 تعداد کل کتاب‌ها: " + libraryManager.getTotalBooks());
        System.out.println("🟢 کتاب‌های موجود: " + libraryManager.getAvailableBooks());
        System.out.println("📖 تعداد کل امانت‌ها: " + libraryManager.getTotalLoans());
        System.out.println("🔴 کتاب‌های امانت داده شده: " + libraryManager.getCurrentLoans());

        List<Loan> pendingLoans = libraryManager.getPendingLoans();
        System.out.println("🟡 درخواست‌های در انتظار تایید: " + pendingLoans.size());

        List<Loan> overdueLoans = libraryManager.getOverdueLoans();
        System.out.println("🚨 امانت‌های معوقه: " + overdueLoans.size());
    }

    private void showOverdueLoans() {
        System.out.println("\n🚨 --- امانت‌های معوقه ---");
        List<Loan> overdueLoans = libraryManager.getOverdueLoans();

        if (overdueLoans.isEmpty()) {
            System.out.println("✅ هیچ امانت معوقه‌ای وجود ندارد.");
            return;
        }

        for (int i = 0; i < overdueLoans.size(); i++) {
            Loan loan = overdueLoans.get(i);
            Book book = libraryManager.findBookById(loan.getBookId());
            Student student = libraryManager.findStudentByUsername(loan.getStudentUsername());
            String bookTitle = (book != null) ? book.getTitle() : "نامشخص";
            String studentName = (student != null) ? student.getName() : loan.getStudentUsername();
            long overdueDays = loan.getOverdueDays();

            System.out.println((i + 1) + ". 🔴 " + studentName +
                    " - 📖 " + bookTitle +
                    " - ⏳ " + overdueDays + " روز تاخیر" +
                    " - 📅 موعد بازگشت: " + loan.getEndDate());
        }
    }

    // Manager Menu Methods
    private void managerLogin() {
        System.out.println("\n👑 --- ورود مدیر سیستم ---");
        System.out.print("📧 نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("🔐 رمز عبور: ");
        String password = scanner.nextLine();

        if (libraryManager.managerLogin(username, password)) {
            System.out.println("✅ ورود موفق! خوش آمدید مدیر سیستم");
            loggedInManagerMenu();
        } else {
            System.out.println("❌ ورود ناموفق! نام کاربری یا رمز عبور اشتباه است.");
        }
    }

    private void loggedInManagerMenu() {
        while (true) {
            System.out.println("\n👑 === منوی مدیر سیستم ===");
            System.out.println("1. 👨‍💼 تعریف کارمند جدید");
            System.out.println("2. 📊 مشاهده عملکرد کارمندان");
            System.out.println("3. 📈 مشاهده آمار امانت‌ها");
            System.out.println("4. 🚨 مشاهده دانشجویان با بیشترین تاخیر");
            System.out.println("5. 📋 مشاهده تمام دانشجویان");
            System.out.println("6. 👥 مشاهده تمام کارمندان");
            System.out.println("7. 📚 مشاهده تمام کتاب‌ها");
            System.out.println("8. 📖 مشاهده تمام امانت‌ها");
            System.out.println("0. 🚪 خروج");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    addEmployee();
                    break;
                case 2:
                    viewEmployeePerformance();
                    break;
                case 3:
                    viewLoanStatistics();
                    break;
                case 4:
                    viewTopDelayedStudents();
                    break;
                case 5:
                    viewAllStudents();
                    break;
                case 6:
                    viewAllEmployees();
                    break;
                case 7:
                    viewAllBooks();
                    break;
                case 8:
                    viewAllLoans();
                    break;
                case 0:
                    libraryManager.logout();
                    return;
                default:
                    System.out.println("❌ گزینه نامعتبر!");
            }
        }
    }

    private void addEmployee() {
        System.out.println("\n👨‍💼 --- تعریف کارمند جدید ---");
        System.out.print("📧 نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("🔐 رمز عبور: ");
        String password = scanner.nextLine();
        System.out.print("🆔 شناسه کارمند: ");
        String employeeId = scanner.nextLine();
        System.out.print("👤 نام کامل: ");
        String name = scanner.nextLine();

        Employee employee = new Employee(username, password, employeeId, name);
        libraryManager.addEmployee(employee);
        System.out.println("✅ کارمند جدید با موفقیت تعریف شد!");
    }

    private void viewEmployeePerformance() {
        System.out.println("\n📊 --- عملکرد کارمندان ---");
        List<Employee> employees = libraryManager.getEmployees();

        if (employees.isEmpty()) {
            System.out.println("❌ هیچ کارمندی ثبت نشده است.");
            return;
        }

        for (Employee employee : employees) {
            EmployeeStatistics stats = libraryManager.getEmployeeStatistics(employee.getUsername());
            System.out.println("👨‍💼 کارمند: " + employee.getName() +
                    " (" + employee.getUsername() + ")");
            System.out.println("   📚 کتاب‌های ثبت شده: " + stats.getBooksRegistered());
            System.out.println("   ✅ امانت‌های تایید شده: " + stats.getLoansApproved());
            System.out.println("   📥 بازگشت‌های ثبت شده: " + stats.getReturnsProcessed());
            System.out.println("🔘 وضعیت: " + (employee.isActive() ? "🟢 فعال" : "🔴 غیرفعال"));
            System.out.println("---");
        }
    }

    private void viewLoanStatistics() {
        System.out.println("\n📈 --- آمار امانت‌ها ---");
        LoanStatistics stats = libraryManager.getLoanStatistics();

        System.out.println("📋 تعداد درخواست‌های امانت ثبت شده: " + stats.getTotalRequests());
        System.out.println("✅ تعداد کل امانت‌های داده شده: " + stats.getTotalApproved());
        System.out.println("📅 میانگین تعداد روزهای امانت: " + String.format("%.2f", stats.getAverageLoanDays()));

        List<Loan> overdueLoans = libraryManager.getOverdueLoans();
        System.out.println("🚨 امانت‌های معوقه: " + overdueLoans.size());

        List<Loan> pendingLoans = libraryManager.getPendingLoans();
        System.out.println("🟡 درخواست‌های در انتظار: " + pendingLoans.size());
    }

    private void viewTopDelayedStudents() {
        System.out.println("\n🚨 --- دانشجویان با بیشترین تاخیر در تحویل کتاب ---");
        List<Student> delayedStudents = libraryManager.getTopDelayedStudents();

        if (delayedStudents.isEmpty()) {
            System.out.println("✅ هیچ دانشجویی با تاخیر یافت نشد.");
            return;
        }

        for (int i = 0; i < delayedStudents.size(); i++) {
            Student student = delayedStudents.get(i);
            StudentStatistics stats = libraryManager.getStudentStatistics(student.getUsername());
            System.out.println((i + 1) + ". 👨‍🎓 " + student.getName() +
                    " (" + student.getUsername() + ")" +
                    " - ⚠️  تعداد تاخیرها: " + stats.getDelayedReturns() +
                    " - 🔘 وضعیت: " + (student.isActive() ? "🟢 فعال" : "🔴 غیرفعال"));
        }
    }

    private void viewAllStudents() {
        System.out.println("\n📋 --- لیست تمام دانشجویان ---");
        List<Student> students = libraryManager.getStudents();

        if (students.isEmpty()) {
            System.out.println("❌ هیچ دانشجویی ثبت نشده است.");
            return;
        }

        for (int i = 0; i < students.size(); i++) {
            Student student = students.get(i);
            StudentStatistics stats = libraryManager.getStudentStatistics(student.getUsername());
            System.out.println((i + 1) + ". 👨‍🎓 " + student.getName() +
                    " - 📧 " + student.getUsername() +
                    " - 🎓 " + student.getStudentId() +
                    " - 📖 امانت‌ها: " + stats.getTotalLoans() +
                    " - ⚠️  تاخیرها: " + stats.getDelayedReturns() +
                    " - 🔘 " + (student.isActive() ? "🟢 فعال" : "🔴 غیرفعال"));
        }
    }

    private void viewAllEmployees() {
        System.out.println("\n👥 --- لیست تمام کارمندان ---");
        List<Employee> employees = libraryManager.getEmployees();

        if (employees.isEmpty()) {
            System.out.println("❌ هیچ کارمندی ثبت نشده است.");
            return;
        }

        for (int i = 0; i < employees.size(); i++) {
            Employee employee = employees.get(i);
            System.out.println((i + 1) + ". 👨‍💼 " + employee.getName() +
                    " - 📧 " + employee.getUsername() +
                    " - 🆔 " + employee.getEmployeeId() +
                    " - 🔘 " + (employee.isActive() ? "🟢 فعال" : "🔴 غیرفعال"));
        }
    }

    private void viewAllBooks() {
        System.out.println("\n📚 --- لیست تمام کتاب‌ها ---");
        List<Book> books = libraryManager.getBooks();

        if (books.isEmpty()) {
            System.out.println("❌ هیچ کتابی ثبت نشده است.");
            return;
        }

        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            String status = book.isAvailable() ? "🟢 موجود" : "🔴 امانت داده شده";
            System.out.println((i + 1) + ". 📖 " + book.getTitle() +
                    " - 👤 " + book.getAuthor() +
                    " - 🏢 " + book.getPublisher() +
                    " - 📅 " + book.getPublicationYear() +
                    " - 🆔 " + book.getBookId() +
                    " - " + status);
        }
    }

    private void viewAllLoans() {
        System.out.println("\n📖 --- لیست تمام امانت‌ها ---");
        List<Loan> loans = libraryManager.getLoans();

        if (loans.isEmpty()) {
            System.out.println("❌ هیچ امانتی ثبت نشده است.");
            return;
        }

        for (int i = 0; i < loans.size(); i++) {
            Loan loan = loans.get(i);
            Book book = libraryManager.findBookById(loan.getBookId());
            Student student = libraryManager.findStudentByUsername(loan.getStudentUsername());
            String bookTitle = (book != null) ? book.getTitle() : "نامشخص";
            String studentName = (student != null) ? student.getName() : loan.getStudentUsername();
            String statusIcon = "🟡";
            if ("APPROVED".equals(loan.getStatus()))
                statusIcon = "🟢";
            else if ("RETURNED".equals(loan.getStatus()))
                statusIcon = "🔵";
            else if (loan.isOverdue())
                statusIcon = "🔴";

            System.out.println((i + 1) + ". " + statusIcon + " 👨‍🎓 " + studentName +
                    " - 📖 " + bookTitle +
                    " - 📅 " + loan.getStartDate() + " تا " + loan.getEndDate() +
                    " - وضعیت: " + getStatusText(loan.getStatus()) +
                    (loan.getActualReturnDate() != null ? " - بازگشت: " + loan.getActualReturnDate() : ""));
        }
    }

    // Utility Methods
    private String getStatusText(String status) {
        switch (status) {
            case "PENDING":
                return "در انتظار تایید";
            case "APPROVED":
                return "تایید شده";
            case "RETURNED":
                return "بازگردانده شده";
            case "OVERDUE":
                return "معوقه";
            default:
                return status;
        }
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("❌ لطفا یک عدد وارد کنید: ");
            }
        }
    }

    private Integer getIntInputOrNull() {
        String input = scanner.nextLine();
        if (input.isEmpty())
            return null;
        try {
            return Integer.parseInt(input);
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private LocalDate getDateInput() {
        while (true) {
            try {
                return LocalDate.parse(scanner.nextLine());
            } catch (Exception e) {
                System.out.print("❌ لطفا تاریخ را به فرمت YYYY-MM-DD وارد کنید: ");
            }
        }
    }

    public static void main(String[] args) {
        LibraryManagementSystem system = new LibraryManagementSystem();
        system.run();
    }
}
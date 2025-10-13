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
        while (true) {
            System.out.println("\n=== سیستم مدیریت کتابخانه دانشگاه ===");
            System.out.println("1. دانشجو");
            System.out.println("2. کاربر مهمان");
            System.out.println("3. کارمند کتابخانه");
            System.out.println("4. مدیر سیستم");
            System.out.println("0. خروج");
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
                    employeeMenu();
                    break;
                case 4:
                    managerMenu();
                    break;
                case 0:
                    System.out.println("خروج از سیستم...");
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void studentMenu() {
        System.out.println("\n=== منوی دانشجو ===");
        System.out.println("1. ثبت نام");
        System.out.println("2. ورود");
        System.out.print("انتخاب: ");

        int choice = getIntInput();
        switch (choice) {
            case 1:
                registerStudent();
                break;
            case 2:
                loginStudent();
                break;
            default:
                System.out.println("گزینه نامعتبر!");
        }
    }

    private void registerStudent() {
        System.out.println("\n--- ثبت نام دانشجو ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();
        System.out.print("شماره دانشجویی: ");
        String studentId = scanner.nextLine();
        System.out.print("نام کامل: ");
        String name = scanner.nextLine();
        System.out.print("ایمیل: ");
        String email = scanner.nextLine();

        if (libraryManager.registerStudent(username, password, studentId, name, email)) {
            System.out.println("ثبت نام با موفقیت انجام شد!");
        } else {
            System.out.println("خطا در ثبت نام! نام کاربری یا شماره دانشجویی تکراری است.");
        }
    }

    private void loginStudent() {
        System.out.println("\n--- ورود دانشجو ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();

        if (libraryManager.studentLogin(username, password)) {
            System.out.println("ورود موفق!");
            loggedInStudentMenu();
        } else {
            System.out.println("ورود ناموفق! نام کاربری یا رمز عبور اشتباه است.");
        }
    }

    private void loggedInStudentMenu() {
        while (true) {
            System.out.println("\n=== منوی دانشجوی وارد شده ===");
            System.out.println("1. جستجوی کتاب");
            System.out.println("2. درخواست امانت کتاب");
            System.out.println("0. خروج");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    searchBooksStudent();
                    break;
                case 2:
                    requestLoan();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void searchBooksStudent() {
        System.out.println("\n--- جستجوی کتاب ---");
        System.out.print("عنوان کتاب (اختیاری): ");
        String title = scanner.nextLine();
        System.out.print("نام نویسنده (اختیاری): ");
        String author = scanner.nextLine();
        System.out.print("سال نشر (اختیاری): ");
        Integer year = getIntInputOrNull();

        List<Book> books = libraryManager.searchBooks(
                title.isEmpty() ? null : title,
                author.isEmpty() ? null : author,
                year);

        System.out.println("\nنتایج جستجو:");
        if (books.isEmpty()) {
            System.out.println("کتابی یافت نشد.");
        } else {
            for (Book book : books) {
                System.out.println(book.getTitle() + " - " + book.getAuthor() +
                        " (" + book.getPublicationYear() + ") - " +
                        (book.isAvailable() ? "موجود" : "امانت داده شده"));
            }
        }
    }

    private void requestLoan() {
        System.out.println("\n--- درخواست امانت کتاب ---");
        System.out.print("شناسه کتاب: ");
        String bookId = scanner.nextLine();

        System.out.print("تاریخ شروع (YYYY-MM-DD): ");
        LocalDate startDate = getDateInput();
        System.out.print("تاریخ پایان (YYYY-MM-DD): ");
        LocalDate endDate = getDateInput();

        if (libraryManager.requestLoan(bookId, startDate, endDate)) {
            System.out.println("درخواست امانت با موفقیت ثبت شد!");
        } else {
            System.out.println("خطا در ثبت درخواست امانت!");
        }
    }

    private void guestMenu() {
        while (true) {
            System.out.println("\n=== منوی کاربر مهمان ===");
            System.out.println("1. مشاهده تعداد دانشجویان");
            System.out.println("2. جستجوی کتاب بر اساس نام");
            System.out.println("3. مشاهده آمار کلی");
            System.out.println("0. بازگشت");

            int choice = getIntInput();
            switch (choice) {
                case 1:
                    System.out.println("تعداد دانشجویان ثبت‌نام شده: " + libraryManager.getTotalStudents());
                    break;
                case 2:
                    searchBooksGuest();
                    break;
                case 3:
                    showGuestStatistics();
                    break;
                case 0:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void searchBooksGuest() {
        System.out.print("نام کتاب: ");
        String title = scanner.nextLine();

        List<Book> books = libraryManager.searchBooksByTitle(title);
        System.out.println("\nنتایج جستجو:");
        if (books.isEmpty()) {
            System.out.println("کتابی یافت نشد.");
        } else {
            for (Book book : books) {
                System.out.println(book.getTitle() + " - " + book.getAuthor() +
                        " - " + book.getPublisher() + " (" + book.getPublicationYear() + ")");
            }
        }
    }

    private void showGuestStatistics() {
        System.out.println("\n--- آمار کلی کتابخانه ---");
        System.out.println("تعداد کل دانشجویان: " + libraryManager.getTotalStudents());
        System.out.println("تعداد کل کتاب‌ها: " + libraryManager.getTotalBooks());
        System.out.println("تعداد کل امانت‌ها: " + libraryManager.getTotalLoans());
        System.out.println("تعداد کتاب‌های امانت داده شده: " + libraryManager.getCurrentLoans());
    }

    private void employeeMenu() {
        System.out.println("\n--- ورود کارمند ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();

        if (libraryManager.employeeLogin(username, password)) {
            System.out.println("ورود موفق!");
            loggedInEmployeeMenu();
        } else {
            System.out.println("ورود ناموفق!");
        }
    }

    private void loggedInEmployeeMenu() {
        while (true) {
            System.out.println("\n=== منوی کارمند ===");
            System.out.println("1. تغییر رمز عبور");
            System.out.println("2. ثبت کتاب جدید");
            System.out.println("3. جستجو و ویرایش کتاب");
            System.out.println("4. بررسی درخواست‌های امانت");
            System.out.println("5. مشاهده تاریخچه امانت دانشجو");
            System.out.println("6. فعال/غیرفعال کردن دانشجو");
            System.out.println("7. ثبت بازگشت کتاب");
            System.out.println("0. خروج");

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
                case 0:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void changePassword() {
        System.out.print("رمز عبور جدید: ");
        String newPassword = scanner.nextLine();
        libraryManager.getCurrentUser().setPassword(newPassword);
        System.out.println("رمز عبور با موفقیت تغییر یافت!");
    }

    private void addBook() {
        System.out.println("\n--- ثبت کتاب جدید ---");
        System.out.print("شناسه کتاب: ");
        String bookId = scanner.nextLine();
        System.out.print("عنوان: ");
        String title = scanner.nextLine();
        System.out.print("نویسنده: ");
        String author = scanner.nextLine();
        System.out.print("ناشر: ");
        String publisher = scanner.nextLine();
        System.out.print("سال نشر: ");
        int year = getIntInput();
        System.out.print("شابک: ");
        String isbn = scanner.nextLine();

        Book book = new Book(bookId, title, author, publisher, year, isbn);
        if (libraryManager.addBook(book)) {
            System.out.println("کتاب با موفقیت ثبت شد!");
        } else {
            System.out.println("خطا در ثبت کتاب! شناسه یا شابک تکراری است.");
        }
    }

    private void searchAndEditBook() {
        System.out.println("\n--- جستجو و ویرایش کتاب ---");
        System.out.print("عنوان کتاب برای جستجو: ");
        String title = scanner.nextLine();

        List<Book> books = libraryManager.searchBooksByTitle(title);
        if (books.isEmpty()) {
            System.out.println("کتابی یافت نشد.");
            return;
        }

        System.out.println("\nکتاب‌های یافت شده:");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.println((i + 1) + ". " + book.getTitle() + " - " + book.getAuthor() +
                    " (" + book.getPublicationYear() + ") - ID: " + book.getBookId());
        }

        System.out.print("شماره کتاب برای ویرایش (0 برای بازگشت): ");
        int choice = getIntInput();
        if (choice == 0 || choice > books.size()) {
            return;
        }

        Book selectedBook = books.get(choice - 1);
        editBook(selectedBook.getBookId());
    }

    private void editBook(String bookId) {
        System.out.println("\n--- ویرایش کتاب ---");
        System.out.print("عنوان جدید: ");
        String title = scanner.nextLine();
        System.out.print("نویسنده جدید: ");
        String author = scanner.nextLine();
        System.out.print("ناشر جدید: ");
        String publisher = scanner.nextLine();
        System.out.print("سال نشر جدید: ");
        int year = getIntInput();
        System.out.print("شابک جدید: ");
        String isbn = scanner.nextLine();

        Book updatedBook = new Book(bookId, title, author, publisher, year, isbn);
        if (libraryManager.updateBook(bookId, updatedBook)) {
            System.out.println("کتاب با موفقیت ویرایش شد!");
        } else {
            System.out.println("خطا در ویرایش کتاب!");
        }
    }

    private void reviewLoanRequests() {
        System.out.println("\n--- بررسی درخواست‌های امانت ---");
        List<Loan> pendingLoans = libraryManager.getPendingLoans();

        if (pendingLoans.isEmpty()) {
            System.out.println("هیچ درخواست امانت در حال انتظار وجود ندارد.");
            return;
        }

        System.out.println("درخواست‌های در حال انتظار:");
        for (int i = 0; i < pendingLoans.size(); i++) {
            Loan loan = pendingLoans.get(i);
            System.out.println((i + 1) + ". دانشجو: " + loan.getStudentUsername() +
                    " - کتاب: " + loan.getBookId() +
                    " - تاریخ شروع: " + loan.getStartDate());
        }

        System.out.print("شماره درخواست برای تایید (0 برای بازگشت): ");
        int choice = getIntInput();
        if (choice == 0 || choice > pendingLoans.size()) {
            return;
        }

        Loan selectedLoan = pendingLoans.get(choice - 1);
        if (libraryManager.approveLoan(selectedLoan.getLoanId(), libraryManager.getCurrentUser().getUsername())) {
            System.out.println("درخواست امانت با موفقیت تایید شد!");
        } else {
            System.out.println("خطا در تایید درخواست امانت!");
        }
    }

    private void viewStudentLoanHistory() {
        System.out.println("\n--- مشاهده تاریخچه امانت دانشجو ---");
        System.out.print("نام کاربری دانشجو: ");
        String username = scanner.nextLine();

        List<Loan> studentLoans = libraryManager.getStudentLoanHistory(username);
        StudentStatistics stats = libraryManager.getStudentStatistics(username);

        System.out.println("\nآمار دانشجو:");
        System.out.println("تعداد کل امانت‌ها: " + stats.getTotalLoans());
        System.out.println("تعداد کتاب‌های تحویل داده نشده: " + stats.getNotReturned());
        System.out.println("تعداد امانت‌های با تاخیر: " + stats.getDelayedReturns());

        System.out.println("\nتاریخچه امانت‌ها:");
        if (studentLoans.isEmpty()) {
            System.out.println("هیچ امانتی یافت نشد.");
        } else {
            for (Loan loan : studentLoans) {
                System.out.println("کتاب: " + loan.getBookId() +
                        " - وضعیت: " + loan.getStatus() +
                        " - شروع: " + loan.getStartDate() +
                        " - پایان: " + loan.getEndDate() +
                        (loan.getActualReturnDate() != null ? " - بازگشت: " + loan.getActualReturnDate() : ""));
            }
        }
    }

    private void toggleStudentStatus() {
        System.out.println("\n--- فعال/غیرفعال کردن دانشجو ---");
        System.out.print("نام کاربری دانشجو: ");
        String username = scanner.nextLine();

        if (libraryManager.toggleStudentStatus(username)) {
            System.out.println("وضعیت دانشجو با موفقیت تغییر یافت!");
        } else {
            System.out.println("خطا در تغییر وضعیت دانشجو!");
        }
    }

    private void returnBook() {
        System.out.println("\n--- ثبت بازگشت کتاب ---");
        System.out.print("شناسه امانت: ");
        String loanId = scanner.nextLine();

        if (libraryManager.returnBook(loanId)) {
            System.out.println("بازگشت کتاب با موفقیت ثبت شد!");
        } else {
            System.out.println("خطا در ثبت بازگشت کتاب!");
        }
    }

    private void managerMenu() {
        System.out.println("\n--- ورود مدیر ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();

        if (username.equals("manager") && password.equals("manager123")) {
            System.out.println("ورود موفق!");
            loggedInManagerMenu();
        } else {
            System.out.println("ورود ناموفق!");
        }
    }

    private void loggedInManagerMenu() {
        while (true) {
            System.out.println("\n=== منوی مدیر ===");
            System.out.println("1. تعریف کارمند جدید");
            System.out.println("2. مشاهده عملکرد کارمندان");
            System.out.println("3. مشاهده آمار امانت‌ها");
            System.out.println("4. مشاهده دانشجویان با بیشترین تاخیر");
            System.out.println("0. خروج");

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
                case 0:
                    return;
                default:
                    System.out.println("گزینه نامعتبر!");
            }
        }
    }

    private void addEmployee() {
        System.out.println("\n--- تعریف کارمند جدید ---");
        System.out.print("نام کاربری: ");
        String username = scanner.nextLine();
        System.out.print("رمز عبور: ");
        String password = scanner.nextLine();
        System.out.print("شناسه کارمند: ");
        String employeeId = scanner.nextLine();
        System.out.print("نام کامل: ");
        String name = scanner.nextLine();

        Employee employee = new Employee(username, password, employeeId, name);
        libraryManager.addEmployee(employee);
        System.out.println("کارمند جدید با موفقیت تعریف شد!");
    }

    private void viewEmployeePerformance() {
        System.out.println("\n--- عملکرد کارمندان ---");
        List<Employee> employees = libraryManager.getEmployees();

        if (employees.isEmpty()) {
            System.out.println("هیچ کارمندی ثبت نشده است.");
            return;
        }

        for (Employee employee : employees) {
            EmployeeStatistics stats = libraryManager.getEmployeeStatistics(employee.getUsername());
            System.out.println("کارمند: " + employee.getName() +
                    " - کتاب‌های ثبت شده: " + stats.getBooksRegistered() +
                    " - امانت‌های تایید شده: " + stats.getLoansApproved() +
                    " - بازگشت‌های ثبت شده: " + stats.getReturnsProcessed());
        }
    }

    private void viewLoanStatistics() {
        System.out.println("\n--- آمار امانت‌ها ---");
        LoanStatistics stats = libraryManager.getLoanStatistics();

        System.out.println("تعداد درخواست‌های امانت ثبت شده: " + stats.getTotalRequests());
        System.out.println("تعداد کل امانت‌های داده شده: " + stats.getTotalApproved());
        System.out.println("میانگین تعداد روزهای امانت: " + String.format("%.2f", stats.getAverageLoanDays()));
    }

    private void viewTopDelayedStudents() {
        System.out.println("\n--- دانشجویان با بیشترین تاخیر در تحویل کتاب ---");
        List<Student> delayedStudents = libraryManager.getTopDelayedStudents();

        if (delayedStudents.isEmpty()) {
            System.out.println("هیچ دانشجویی با تاخیر یافت نشد.");
            return;
        }

        for (int i = 0; i < delayedStudents.size(); i++) {
            Student student = delayedStudents.get(i);
            StudentStatistics stats = libraryManager.getStudentStatistics(student.getUsername());
            System.out.println((i + 1) + ". " + student.getName() +
                    " (" + student.getUsername() + ")" +
                    " - تعداد تاخیرها: " + stats.getDelayedReturns());
        }
    }

    private int getIntInput() {
        while (true) {
            try {
                return Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.print("لطفا یک عدد وارد کنید: ");
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
                System.out.print("لطفا تاریخ را به فرمت YYYY-MM-DD وارد کنید: ");
            }
        }
    }

    public static void main(String[] args) {
        LibraryManagementSystem system = new LibraryManagementSystem();
        system.run();
    }
}
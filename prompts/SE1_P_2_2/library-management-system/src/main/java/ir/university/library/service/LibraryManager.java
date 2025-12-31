package ir.university.library.service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import ir.university.library.model.*;
import ir.university.library.statistics.*;

public class LibraryManager {
    private List<Student> students;
    private List<Employee> employees;
    private List<Book> books;
    private List<Loan> loans;
    private User currentUser;

    public LibraryManager() {
        loadData();
        if (FileManager.isFirstRun()) {
            initializeDefaultData();
        }
    }

    private void initializeDefaultData() {
        System.out.println("📦 در حال راه‌اندازی اولیه سیستم...");

        // ایجاد مدیر سیستم
        Employee manager = new Employee("manager", "manager123", "M001", "مدیر سیستم");
        employees.add(manager);

        // ایجاد کارمندان پیش‌فرض
        Employee emp1 = new Employee("emp1", "1234", "E001", "کارمند اول");
        Employee emp2 = new Employee("emp2", "1234", "E002", "کارمند دوم");
        Employee emp3 = new Employee("emp3", "1234", "E003", "کارمند سوم");

        employees.add(emp1);
        employees.add(emp2);
        employees.add(emp3);

        // ایجاد کتاب‌های نمونه
        Book book1 = new Book("B001", "آموزش برنامه‌نویسی جاوا", "دکتر علیرضا محمدی", "نشر دانش", 2023,
                "978-600-00-0000-1");
        Book book2 = new Book("B002", "پایگاه داده پیشرفته", "دکتر فاطمه احمدی", "نشر علم", 2022, "978-600-00-0000-2");
        Book book3 = new Book("B003", "هوش مصنوعی و یادگیری ماشین", "دکتر محمد رضایی", "نشر پیشرو", 2024,
                "978-600-00-0000-3");
        Book book4 = new Book("B004", "طراحی الگوریتم", "دکتر سارا کریمی", "نشر دانشگاهی", 2021, "978-600-00-0000-4");
        Book book5 = new Book("B005", "شبکه‌های کامپیوتری", "دکتر احمد حسینی", "نشر فنی", 2023, "978-600-00-0000-5");

        books.add(book1);
        books.add(book2);
        books.add(book3);
        books.add(book4);
        books.add(book5);

        // ایجاد دانشجویان نمونه
        Student student1 = new Student("stu1", "1234", "S001", "محمد دانشجو", "mohammad@university.ac.ir");
        Student student2 = new Student("stu2", "1234", "S002", "فاطمه دانشجو", "fatemeh@university.ac.ir");
        Student student3 = new Student("stu3", "1234", "S003", "علی دانشجو", "ali@university.ac.ir");

        students.add(student1);
        students.add(student2);
        students.add(student3);

        saveData();
        System.out.println("✅ راه‌اندازی اولیه با موفقیت انجام شد!");
    }

    private void loadData() {
        students = FileManager.loadStudents();
        employees = FileManager.loadEmployees();
        books = FileManager.loadBooks();
        loans = FileManager.loadLoans();
    }

    private void saveData() {
        FileManager.saveStudents(students);
        FileManager.saveEmployees(employees);
        FileManager.saveBooks(books);
        FileManager.saveLoans(loans);
    }

    // ==================== Authentication ====================
    public boolean studentLogin(String username, String password) {
        for (Student student : students) {
            if (student.getUsername().equals(username) &&
                    student.getPassword().equals(password) &&
                    student.isActive()) {
                currentUser = student;
                return true;
            }
        }
        return false;
    }

    public boolean employeeLogin(String username, String password) {
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username) &&
                    employee.getPassword().equals(password) &&
                    employee.isActive()) {
                currentUser = employee;
                return true;
            }
        }
        return false;
    }

    public boolean managerLogin(String username, String password) {
        if (username.equals("manager") && password.equals("manager123")) {
            for (Employee employee : employees) {
                if (employee.getUsername().equals("manager")) {
                    currentUser = employee;
                    return true;
                }
            }
        }
        return false;
    }

    // ==================== Student Registration ====================
    public boolean registerStudent(String username, String password,
            String studentId, String name, String email) {
        // بررسی تکراری نبودن
        for (Student student : students) {
            if (student.getUsername().equals(username) || student.getStudentId().equals(studentId)) {
                return false;
            }
        }

        Student student = new Student(username, password, studentId, name, email);
        students.add(student);
        saveData();
        return true;
    }

    // ==================== Employee Management ====================
    public boolean addEmployee(Employee employee) {
        for (Employee emp : employees) {
            if (emp.getUsername().equals(employee.getUsername()) ||
                    emp.getEmployeeId().equals(employee.getEmployeeId())) {
                return false;
            }
        }
        employees.add(employee);
        saveData();
        return true;
    }

    // ==================== Book Management ====================
    public List<Book> searchBooks(String title, String author, Integer year) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            boolean matches = true;
            if (title != null && !title.isEmpty() &&
                    !book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                matches = false;
            }
            if (author != null && !author.isEmpty() &&
                    !book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                matches = false;
            }
            if (year != null && book.getPublicationYear() != year) {
                matches = false;
            }
            if (matches) {
                result.add(book);
            }
        }
        return result;
    }

    public List<Book> searchBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        return result;
    }

    public Book findBookById(String bookId) {
        for (Book book : books) {
            if (book.getBookId().equals(bookId)) {
                return book;
            }
        }
        return null;
    }

    public boolean addBook(Book book) {
        for (Book b : books) {
            if (b.getBookId().equals(book.getBookId())) {
                return false;
            }
            if (b.getIsbn().equals(book.getIsbn())) {
                return false;
            }
        }
        books.add(book);
        saveData();
        return true;
    }

    public boolean updateBook(String bookId, Book updatedBook) {
        for (Book book : books) {
            if (book.getBookId().equals(bookId)) {
                book.setTitle(updatedBook.getTitle());
                book.setAuthor(updatedBook.getAuthor());
                book.setPublisher(updatedBook.getPublisher());
                book.setPublicationYear(updatedBook.getPublicationYear());
                book.setIsbn(updatedBook.getIsbn());
                saveData();
                return true;
            }
        }
        return false;
    }

    public boolean deleteBook(String bookId) {
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            if (book.getBookId().equals(bookId)) {
                for (Loan loan : loans) {
                    if (loan.getBookId().equals(bookId) &&
                            ("PENDING".equals(loan.getStatus()) || "APPROVED".equals(loan.getStatus()))) {
                        return false;
                    }
                }
                books.remove(i);
                saveData();
                return true;
            }
        }
        return false;
    }

    // ==================== Loan Management ====================
    public boolean requestLoan(String bookId, LocalDate startDate, LocalDate endDate) {
        if (!(currentUser instanceof Student))
            return false;

        Student student = (Student) currentUser;
        if (!student.isActive()) {
            return false;
        }

        Book foundBook = null;
        for (Book book : books) {
            if (book.getBookId().equals(bookId) && book.isAvailable()) {
                foundBook = book;
                break;
            }
        }

        if (foundBook == null) {
            return false;
        }

        if (startDate.isBefore(LocalDate.now())) {
            return false;
        }

        if (endDate.isBefore(startDate)) {
            return false;
        }

        String loanId = "LOAN" + System.currentTimeMillis();
        Loan loan = new Loan(loanId, student.getUsername(), bookId, startDate, endDate);
        loans.add(loan);
        saveData();
        return true;
    }

    public List<Loan> getPendingLoans() {
        List<Loan> result = new ArrayList<>();
        LocalDate today = LocalDate.now();
        for (Loan loan : loans) {
            if ("PENDING".equals(loan.getStatus()) &&
                    (loan.getStartDate().isEqual(today) ||
                            loan.getStartDate().isBefore(today))) {
                result.add(loan);
            }
        }
        return result;
    }

    public boolean approveLoan(String loanId, String employeeUsername) {
        Loan foundLoan = null;
        for (Loan loan : loans) {
            if (loan.getLoanId().equals(loanId) && "PENDING".equals(loan.getStatus())) {
                foundLoan = loan;
                break;
            }
        }

        if (foundLoan == null) {
            return false;
        }

        Book foundBook = null;
        for (Book book : books) {
            if (book.getBookId().equals(foundLoan.getBookId()) && book.isAvailable()) {
                foundBook = book;
                break;
            }
        }

        if (foundBook == null) {
            return false;
        }

        foundBook.setAvailable(false);
        foundLoan.setStatus("APPROVED");
        foundLoan.setEmployeeUsername(employeeUsername);
        saveData();
        return true;
    }

    // متد جدید برای رد کردن درخواست امانت
    public boolean rejectLoan(String loanId, String employeeUsername) {
        for (Loan loan : loans) {
            if (loan.getLoanId().equals(loanId) && "PENDING".equals(loan.getStatus())) {
                loan.setStatus("REJECTED");
                loan.setEmployeeUsername(employeeUsername);
                saveData();
                return true;
            }
        }
        return false;
    }

    public boolean returnBook(String loanId) {
        Loan foundLoan = null;
        for (Loan loan : loans) {
            if (loan.getLoanId().equals(loanId) && "APPROVED".equals(loan.getStatus())) {
                foundLoan = loan;
                break;
            }
        }

        if (foundLoan == null) {
            return false;
        }

        Book foundBook = null;
        for (Book book : books) {
            if (book.getBookId().equals(foundLoan.getBookId())) {
                foundBook = book;
                break;
            }
        }

        if (foundBook != null) {
            foundBook.setAvailable(true);
            foundLoan.setStatus("RETURNED");
            foundLoan.setActualReturnDate(LocalDate.now());
            saveData();
            return true;
        }
        return false;
    }

    // ==================== Student Management ====================
    public boolean toggleStudentStatus(String username) {
        for (Student student : students) {
            if (student.getUsername().equals(username)) {
                student.setActive(!student.isActive());
                saveData();
                return true;
            }
        }
        return false;
    }

    public Student findStudentByUsername(String username) {
        for (Student student : students) {
            if (student.getUsername().equals(username)) {
                return student;
            }
        }
        return null;
    }

    public Employee findEmployeeByUsername(String username) {
        for (Employee employee : employees) {
            if (employee.getUsername().equals(username)) {
                return employee;
            }
        }
        return null;
    }

    // ==================== Statistics ====================
    public int getTotalStudents() {
        return students.size();
    }

    public int getActiveStudents() {
        int count = 0;
        for (Student student : students) {
            if (student.isActive())
                count++;
        }
        return count;
    }

    public int getTotalBooks() {
        return books.size();
    }

    public int getAvailableBooks() {
        int count = 0;
        for (Book book : books) {
            if (book.isAvailable())
                count++;
        }
        return count;
    }

    public int getTotalLoans() {
        return loans.size();
    }

    public int getCurrentLoans() {
        int count = 0;
        for (Loan loan : loans) {
            if ("APPROVED".equals(loan.getStatus())) {
                count++;
            }
        }
        return count;
    }

    public List<Loan> getStudentLoanHistory(String studentUsername) {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.getStudentUsername().equals(studentUsername)) {
                result.add(loan);
            }
        }
        return result;
    }

    public List<Loan> getOverdueLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if (loan.isOverdue()) {
                result.add(loan);
            }
        }
        return result;
    }

    public StudentStatistics getStudentStatistics(String studentUsername) {
        List<Loan> studentLoans = getStudentLoanHistory(studentUsername);

        long totalLoans = studentLoans.size();
        long notReturned = 0;
        long delayedReturns = 0;

        for (Loan loan : studentLoans) {
            if ("APPROVED".equals(loan.getStatus())) {
                notReturned++;
            }
            if (loan.isOverdue()) {
                delayedReturns++;
            }
        }

        return new StudentStatistics(totalLoans, notReturned, delayedReturns);
    }

    public EmployeeStatistics getEmployeeStatistics(String employeeUsername) {
        long loansApproved = 0;
        long returnsProcessed = 0;

        for (Loan loan : loans) {
            if (employeeUsername.equals(loan.getEmployeeUsername())) {
                loansApproved++;
                if ("RETURNED".equals(loan.getStatus())) {
                    returnsProcessed++;
                }
            }
        }

        // تعداد کتاب‌های ثبت شده توسط کارمند
        long booksRegistered = books.size() / Math.max(employees.size(), 1);

        return new EmployeeStatistics(booksRegistered, loansApproved, returnsProcessed);
    }

    public LoanStatistics getLoanStatistics() {
        long totalRequests = loans.size();
        long totalApproved = 0;
        long totalDays = 0;
        long returnedCount = 0;

        for (Loan loan : loans) {
            if ("APPROVED".equals(loan.getStatus()) || "RETURNED".equals(loan.getStatus())) {
                totalApproved++;
            }
            if ("RETURNED".equals(loan.getStatus()) && loan.getActualReturnDate() != null) {
                long days = java.time.temporal.ChronoUnit.DAYS.between(
                        loan.getStartDate(), loan.getActualReturnDate());
                totalDays += days;
                returnedCount++;
            }
        }

        double avgLoanDays = returnedCount > 0 ? (double) totalDays / returnedCount : 0.0;

        return new LoanStatistics(totalRequests, totalApproved, avgLoanDays);
    }

    public List<Student> getTopDelayedStudents() {
        Map<Student, Long> delayCounts = new HashMap<>();

        for (Student student : students) {
            long delays = getStudentLoanHistory(student.getUsername()).stream()
                    .filter(Loan::isOverdue)
                    .count();
            if (delays > 0) {
                delayCounts.put(student, delays);
            }
        }

        return delayCounts.entrySet().stream()
                .sorted((e1, e2) -> Long.compare(e2.getValue(), e1.getValue()))
                .limit(10)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    // ==================== Getters ====================
    public List<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public List<Employee> getEmployees() {
        return new ArrayList<>(employees);
    }

    public List<Book> getBooks() {
        return new ArrayList<>(books);
    }

    public List<Loan> getLoans() {
        return new ArrayList<>(loans);
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public void logout() {
        this.currentUser = null;
    }
}
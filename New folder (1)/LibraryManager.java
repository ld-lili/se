import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class LibraryManager {
    private List<Student> students;
    private List<Employee> employees;
    private List<Book> books;
    private List<Loan> loans;
    private User currentUser;

    public LibraryManager() {
        loadData();
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

    // Authentication methods
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

    // Student registration
    public boolean registerStudent(String username, String password,
            String studentId, String name, String email) {
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

    // Book search methods
    public List<Book> searchBooks(String title, String author, Integer year) {
        List<Book> result = new ArrayList<>();
        for (Book book : books) {
            boolean matches = true;
            if (title != null && !book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                matches = false;
            }
            if (author != null && !book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
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

    // Loan management
    public boolean requestLoan(String bookId, LocalDate startDate, LocalDate endDate) {
        if (!(currentUser instanceof Student))
            return false;

        Student student = (Student) currentUser;
        if (!student.isActive())
            return false;

        Book foundBook = null;
        for (Book book : books) {
            if (book.getBookId().equals(bookId) && book.isAvailable()) {
                foundBook = book;
                break;
            }
        }

        if (foundBook == null)
            return false;

        String loanId = "LOAN" + System.currentTimeMillis();
        Loan loan = new Loan(loanId, student.getUsername(), bookId, startDate, endDate);
        loans.add(loan);
        saveData();
        return true;
    }

    public List<Loan> getPendingLoans() {
        List<Loan> result = new ArrayList<>();
        for (Loan loan : loans) {
            if ("PENDING".equals(loan.getStatus()) &&
                    (loan.getStartDate().isEqual(LocalDate.now()) ||
                            loan.getStartDate().isBefore(LocalDate.now()))) {
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

        if (foundLoan != null) {
            Book foundBook = null;
            for (Book book : books) {
                if (book.getBookId().equals(foundLoan.getBookId()) && book.isAvailable()) {
                    foundBook = book;
                    break;
                }
            }

            if (foundBook != null) {
                foundBook.setAvailable(false);
                foundLoan.setStatus("APPROVED");
                foundLoan.setEmployeeUsername(employeeUsername);
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

        if (foundLoan != null) {
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
        }
        return false;
    }

    // Book management
    public boolean addBook(Book book) {
        for (Book b : books) {
            if (b.getBookId().equals(book.getBookId()) || b.getIsbn().equals(book.getIsbn())) {
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

    // Student management
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

    // Statistics methods
    public int getTotalStudents() {
        return students.size();
    }

    public int getTotalBooks() {
        return books.size();
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

        long booksRegistered = books.size(); // Simplified

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

    // Getters
    public List<Student> getStudents() {
        return students;
    }

    public List<Employee> getEmployees() {
        return employees;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Loan> getLoans() {
        return loans;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public void addEmployee(Employee employee) {
        employees.add(employee);
        saveData();
    }
}

// Statistics classes
class StudentStatistics {
    private long totalLoans;
    private long notReturned;
    private long delayedReturns;

    public StudentStatistics(long totalLoans, long notReturned, long delayedReturns) {
        this.totalLoans = totalLoans;
        this.notReturned = notReturned;
        this.delayedReturns = delayedReturns;
    }

    public long getTotalLoans() {
        return totalLoans;
    }

    public long getNotReturned() {
        return notReturned;
    }

    public long getDelayedReturns() {
        return delayedReturns;
    }
}

class EmployeeStatistics {
    private long booksRegistered;
    private long loansApproved;
    private long returnsProcessed;

    public EmployeeStatistics(long booksRegistered, long loansApproved, long returnsProcessed) {
        this.booksRegistered = booksRegistered;
        this.loansApproved = loansApproved;
        this.returnsProcessed = returnsProcessed;
    }

    public long getBooksRegistered() {
        return booksRegistered;
    }

    public long getLoansApproved() {
        return loansApproved;
    }

    public long getReturnsProcessed() {
        return returnsProcessed;
    }
}

class LoanStatistics {
    private long totalRequests;
    private long totalApproved;
    private double averageLoanDays;

    public LoanStatistics(long totalRequests, long totalApproved, double averageLoanDays) {
        this.totalRequests = totalRequests;
        this.totalApproved = totalApproved;
        this.averageLoanDays = averageLoanDays;
    }

    public long getTotalRequests() {
        return totalRequests;
    }

    public long getTotalApproved() {
        return totalApproved;
    }

    public double getAverageLoanDays() {
        return averageLoanDays;
    }
}
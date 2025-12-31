package ir.university.library.api;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpExchange;
import ir.university.library.service.LibraryManager;
import ir.university.library.model.*;
import ir.university.library.statistics.*;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

public class ApiServer {
    private LibraryManager libraryManager;
    private HttpServer server;
    private Map<String, String> activeTokens;
    private final int PORT = 8080;

    public ApiServer(LibraryManager libraryManager) throws IOException {
        this.libraryManager = libraryManager;
        this.activeTokens = new HashMap<>();
        this.server = HttpServer.create(new InetSocketAddress(PORT), 0);
        setupEndpoints();
    }

    public void start() {
        server.setExecutor(Executors.newCachedThreadPool());
        server.start();
        System.out.println("🚀 API Server started on port " + PORT);
        System.out.println("📡 Endpoints available at http://localhost:" + PORT + "/api");
    }

    public void stop() {
        server.stop(0);
        System.out.println("🛑 API Server stopped");
    }

    private void setupEndpoints() {
        // احراز هویت
        server.createContext("/api/auth/register", this::handleRegister);
        server.createContext("/api/auth/login", this::handleLogin);
        server.createContext("/api/auth/change-password", this::handleChangePassword);

        // کتاب‌ها
        server.createContext("/api/books", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetBooks(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handleCreateBook(exchange);
            }
        });

        server.createContext("/api/books/search", this::handleSearchBooks);

        // کتاب خاص
        server.createContext("/api/books/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length >= 4) {
                String bookId = parts[3];
                if ("GET".equals(exchange.getRequestMethod())) {
                    handleGetBookById(exchange, bookId);
                } else if ("PUT".equals(exchange.getRequestMethod())) {
                    handleUpdateBook(exchange, bookId);
                }
            }
        });

        // امانت کتاب
        server.createContext("/api/borrow/request", this::handleBorrowRequest);
        server.createContext("/api/borrow/requests/pending", this::handlePendingRequests);

        // تایید/رد درخواست
        server.createContext("/api/borrow/requests/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length >= 5) {
                String loanId = parts[4];
                String action = parts.length > 5 ? parts[5] : "";
                if ("approve".equals(action)) {
                    handleApproveLoan(exchange, loanId);
                } else if ("reject".equals(action)) {
                    handleRejectLoan(exchange, loanId);
                }
            }
        });

        // بازگشت کتاب
        server.createContext("/api/borrow/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length >= 4 && "return".equals(parts[3])) {
                String loanId = parts[2];
                handleReturnBook(exchange, loanId);
            }
        });

        // دانشجویان
        server.createContext("/api/students/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length >= 4) {
                String studentId = parts[3];
                if (parts.length == 4) {
                    handleGetStudentProfile(exchange, studentId);
                } else if (parts.length >= 5) {
                    String action = parts[4];
                    if ("status".equals(action)) {
                        handleToggleStudentStatus(exchange, studentId);
                    } else if ("borrow-history".equals(action)) {
                        handleStudentBorrowHistory(exchange, studentId);
                    }
                }
            }
        });

        // آمار
        server.createContext("/api/stats/summary", this::handleStatsSummary);
        server.createContext("/api/stats/borrows", this::handleBorrowStats);

        server.createContext("/api/stats/employees/", exchange -> {
            String path = exchange.getRequestURI().getPath();
            String[] parts = path.split("/");
            if (parts.length >= 5 && "performance".equals(parts[4])) {
                String employeeId = parts[3];
                handleEmployeePerformance(exchange, employeeId);
            }
        });

        server.createContext("/api/stats/top-delayed", this::handleTopDelayed);

        // مدیریت
        server.createContext("/api/admin/employees", exchange -> {
            if ("GET".equals(exchange.getRequestMethod())) {
                handleGetEmployees(exchange);
            } else if ("POST".equals(exchange.getRequestMethod())) {
                handleCreateEmployee(exchange);
            }
        });
    }

    // ==================== Handlers ====================

    private void handleRegister(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            Map<String, String> request = parseJsonRequest(exchange);

            boolean success = libraryManager.registerStudent(
                    request.get("username"),
                    request.get("password"),
                    request.get("studentId"),
                    request.get("name"),
                    request.get("email"));

            ApiResponse response = success ? ApiResponse.success("ثبت‌نام موفقیت‌آمیز بود")
                    : ApiResponse.error("نام کاربری یا شماره دانشجویی تکراری است");

            sendJsonResponse(exchange, success ? 201 : 400, response.toMap());
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleLogin(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            Map<String, String> request = parseJsonRequest(exchange);
            String username = request.get("username");
            String password = request.get("password");
            String role = request.getOrDefault("role", "STUDENT"); // اصلاح شده

            if (username == null || password == null) {
                ApiResponse response = ApiResponse.error("نام کاربری یا رمز عبور ارسال نشده است");
                sendJsonResponse(exchange, 400, response.toMap());
                return;
            }

            boolean authenticated = false;
            User user = null;

            switch (role.toUpperCase()) {
                case "STUDENT":
                    authenticated = libraryManager.studentLogin(username, password);
                    user = libraryManager.findStudentByUsername(username);
                    break;
                case "EMPLOYEE":
                    authenticated = libraryManager.employeeLogin(username, password);
                    user = libraryManager.findEmployeeByUsername(username);
                    break;
                case "MANAGER":
                    authenticated = libraryManager.managerLogin(username, password);
                    user = libraryManager.findEmployeeByUsername(username);
                    break;
                default:
                    ApiResponse response = ApiResponse.error("نقش کاربر نامعتبر است");
                    sendJsonResponse(exchange, 400, response.toMap());
                    return;
            }

            if (authenticated && user != null) {
                String token = "token-" + UUID.randomUUID().toString();
                activeTokens.put(token, username + ":" + role);

                Map<String, Object> data = new HashMap<>();
                data.put("token", token);
                data.put("username", username);
                data.put("role", role);
                data.put("name", getFullName(user, role));

                ApiResponse response = ApiResponse.success("ورود موفق", data);
                sendJsonResponse(exchange, 200, response.toMap());
            } else {
                ApiResponse response = ApiResponse.error("نام کاربری یا رمز عبور اشتباه است");
                sendJsonResponse(exchange, 401, response.toMap());
            }
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleChangePassword(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        try {
            String token = getAuthToken(exchange);
            if (!isAuthenticated(token)) {
                sendError(exchange, 401, "Unauthorized");
                return;
            }

            Map<String, String> request = parseJsonRequest(exchange);
            String username = getUserFromToken(token);
            String currentPassword = request.get("currentPassword");
            String newPassword = request.get("newPassword");

            // بررسی null بودن مقادیر
            if (currentPassword == null || newPassword == null) {
                ApiResponse response = ApiResponse.error("رمز عبور فعلی یا جدید ارسال نشده است");
                sendJsonResponse(exchange, 400, response.toMap());
                return;
            }

            // اعتبارسنجی رمز فعلی
            boolean valid = false;
            if (libraryManager.findStudentByUsername(username) != null) {
                valid = libraryManager.studentLogin(username, currentPassword);
            } else if (libraryManager.findEmployeeByUsername(username) != null) {
                valid = libraryManager.employeeLogin(username, currentPassword);
            }

            if (valid) {
                User user = libraryManager.findStudentByUsername(username);
                if (user == null)
                    user = libraryManager.findEmployeeByUsername(username);
                if (user != null) {
                    user.setPassword(newPassword);
                    libraryManager.logout(); // خروج از سیستم بعد از تغییر رمز

                    ApiResponse response = ApiResponse.success("رمز عبور با موفقیت تغییر یافت");
                    sendJsonResponse(exchange, 200, response.toMap());
                } else {
                    ApiResponse response = ApiResponse.error("کاربر یافت نشد");
                    sendJsonResponse(exchange, 404, response.toMap());
                }
            } else {
                ApiResponse response = ApiResponse.error("رمز عبور فعلی اشتباه است");
                sendJsonResponse(exchange, 400, response.toMap());
            }
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleGetBooks(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQueryParams(exchange);

        String title = params.get("title");
        String author = params.get("author");
        String yearStr = params.get("year");
        Integer year = null;
        if (yearStr != null && !yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                // ignore invalid year
            }
        }

        List<Book> books = libraryManager.searchBooks(title, author, year);

        List<Map<String, Object>> bookData = books.stream()
                .map(this::convertBookToMap)
                .collect(Collectors.toList());

        ApiResponse response = ApiResponse.success(bookData);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleGetBookById(HttpExchange exchange, String bookId) throws IOException {
        Book book = libraryManager.findBookById(bookId);

        if (book != null) {
            ApiResponse response = ApiResponse.success(convertBookToMap(book));
            sendJsonResponse(exchange, 200, response.toMap());
        } else {
            ApiResponse response = ApiResponse.error("کتاب یافت نشد");
            sendJsonResponse(exchange, 404, response.toMap());
        }
    }

    private void handleCreateBook(HttpExchange exchange) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        try {
            Map<String, String> request = parseJsonRequest(exchange);

            // بررسی مقادیر اجباری
            if (request.get("bookId") == null || request.get("title") == null ||
                    request.get("author") == null || request.get("publicationYear") == null) {
                ApiResponse response = ApiResponse.error("مقادیر اجباری ارسال نشده‌اند");
                sendJsonResponse(exchange, 400, response.toMap());
                return;
            }

            int year;
            try {
                year = Integer.parseInt(request.get("publicationYear"));
            } catch (NumberFormatException e) {
                ApiResponse response = ApiResponse.error("سال نشر نامعتبر است");
                sendJsonResponse(exchange, 400, response.toMap());
                return;
            }

            Book book = new Book(
                    request.get("bookId"),
                    request.get("title"),
                    request.get("author"),
                    request.getOrDefault("publisher", ""),
                    year,
                    request.getOrDefault("isbn", ""));

            boolean success = libraryManager.addBook(book);

            ApiResponse response = success ? ApiResponse.success("کتاب با موفقیت ایجاد شد", convertBookToMap(book))
                    : ApiResponse.error("شناسه کتاب یا شابک تکراری است");

            sendJsonResponse(exchange, success ? 201 : 400, response.toMap());
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleUpdateBook(HttpExchange exchange, String bookId) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        try {
            Book existingBook = libraryManager.findBookById(bookId);
            if (existingBook == null) {
                ApiResponse response = ApiResponse.error("کتاب یافت نشد");
                sendJsonResponse(exchange, 404, response.toMap());
                return;
            }

            Map<String, String> request = parseJsonRequest(exchange);

            // استفاده از مقادیر جدید یا مقادیر موجود
            String title = request.getOrDefault("title", existingBook.getTitle());
            String author = request.getOrDefault("author", existingBook.getAuthor());
            String publisher = request.getOrDefault("publisher", existingBook.getPublisher());
            String isbn = request.getOrDefault("isbn", existingBook.getIsbn());

            int year;
            String yearStr = request.get("publicationYear");
            if (yearStr != null && !yearStr.isEmpty()) {
                try {
                    year = Integer.parseInt(yearStr);
                } catch (NumberFormatException e) {
                    ApiResponse response = ApiResponse.error("سال نشر نامعتبر است");
                    sendJsonResponse(exchange, 400, response.toMap());
                    return;
                }
            } else {
                year = existingBook.getPublicationYear();
            }

            Book updatedBook = new Book(bookId, title, author, publisher, year, isbn);

            boolean success = libraryManager.updateBook(bookId, updatedBook);

            ApiResponse response = success
                    ? ApiResponse.success("کتاب با موفقیت به‌روزرسانی شد", convertBookToMap(updatedBook))
                    : ApiResponse.error("خطا در به‌روزرسانی کتاب");

            sendJsonResponse(exchange, success ? 200 : 400, response.toMap());
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleSearchBooks(HttpExchange exchange) throws IOException {
        Map<String, String> params = parseQueryParams(exchange);

        String title = params.get("title");
        String author = params.get("author");
        String yearStr = params.get("year");
        Integer year = null;
        if (yearStr != null && !yearStr.isEmpty()) {
            try {
                year = Integer.parseInt(yearStr);
            } catch (NumberFormatException e) {
                // ignore invalid year
            }
        }

        List<Book> books = libraryManager.searchBooks(title, author, year);

        List<Map<String, Object>> bookData = books.stream()
                .map(this::convertBookToMap)
                .collect(Collectors.toList());

        ApiResponse response = ApiResponse.success(bookData);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleBorrowRequest(HttpExchange exchange) throws IOException {
        if (!"POST".equals(exchange.getRequestMethod())) {
            sendError(exchange, 405, "Method Not Allowed");
            return;
        }

        if (!checkAuthorization(exchange, "STUDENT")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        try {
            Map<String, String> request = parseJsonRequest(exchange);

            String bookId = request.get("bookId");
            String startDateStr = request.get("startDate");
            String endDateStr = request.get("endDate");

            if (bookId == null || startDateStr == null || endDateStr == null) {
                ApiResponse response = ApiResponse.error("مقادیر اجباری ارسال نشده‌اند");
                sendJsonResponse(exchange, 400, response.toMap());
                return;
            }

            LocalDate startDate = LocalDate.parse(startDateStr);
            LocalDate endDate = LocalDate.parse(endDateStr);

            boolean success = libraryManager.requestLoan(bookId, startDate, endDate);

            ApiResponse response = success ? ApiResponse.success("درخواست امانت با موفقیت ثبت شد")
                    : ApiResponse.error("خطا در ثبت درخواست امانت");

            sendJsonResponse(exchange, success ? 201 : 400, response.toMap());
        } catch (Exception e) {
            ApiResponse response = ApiResponse.error("فرمت تاریخ نامعتبر است");
            sendJsonResponse(exchange, 400, response.toMap());
        }
    }

    private void handlePendingRequests(HttpExchange exchange) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        List<Loan> pendingLoans = libraryManager.getPendingLoans();

        List<Map<String, Object>> loanData = pendingLoans.stream()
                .map(this::convertLoanToMap)
                .collect(Collectors.toList());

        ApiResponse response = ApiResponse.success(loanData);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleApproveLoan(HttpExchange exchange, String loanId) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        String token = getAuthToken(exchange);
        String employeeUsername = getUserFromToken(token);

        boolean success = libraryManager.approveLoan(loanId, employeeUsername);

        ApiResponse response = success ? ApiResponse.success("درخواست امانت تایید شد")
                : ApiResponse.error("خطا در تایید درخواست امانت");

        sendJsonResponse(exchange, success ? 200 : 400, response.toMap());
    }

    private void handleRejectLoan(HttpExchange exchange, String loanId) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        try {
            String token = getAuthToken(exchange);
            String employeeUsername = getUserFromToken(token);

            boolean success = libraryManager.rejectLoan(loanId, employeeUsername);

            ApiResponse response = success ? ApiResponse.success("درخواست امانت رد شد")
                    : ApiResponse.error("خطا در رد درخواست امانت");

            sendJsonResponse(exchange, success ? 200 : 400, response.toMap());
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleReturnBook(HttpExchange exchange, String loanId) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        boolean success = libraryManager.returnBook(loanId);

        ApiResponse response = success ? ApiResponse.success("بازگشت کتاب با موفقیت ثبت شد")
                : ApiResponse.error("خطا در ثبت بازگشت کتاب");

        sendJsonResponse(exchange, success ? 200 : 400, response.toMap());
    }

    private void handleGetStudentProfile(HttpExchange exchange, String username) throws IOException {
        Student student = libraryManager.findStudentByUsername(username);

        if (student != null) {
            Map<String, Object> studentData = convertStudentToMap(student);

            StudentStatistics stats = libraryManager.getStudentStatistics(username);
            Map<String, Object> statistics = new HashMap<>();
            statistics.put("totalLoans", stats.getTotalLoans());
            statistics.put("notReturned", stats.getNotReturned());
            statistics.put("delayedReturns", stats.getDelayedReturns());

            studentData.put("statistics", statistics);

            ApiResponse response = ApiResponse.success(studentData);
            sendJsonResponse(exchange, 200, response.toMap());
        } else {
            ApiResponse response = ApiResponse.error("دانشجو یافت نشد");
            sendJsonResponse(exchange, 404, response.toMap());
        }
    }

    private void handleToggleStudentStatus(HttpExchange exchange, String username) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        try {
            Map<String, String> request = parseJsonRequest(exchange);
            String activeStr = request.get("active");
            boolean active = "true".equalsIgnoreCase(activeStr);

            Student student = libraryManager.findStudentByUsername(username);
            if (student != null) {
                student.setActive(active);

                Map<String, Object> data = new HashMap<>();
                data.put("username", username);
                data.put("active", active);
                data.put("message", "وضعیت دانشجو با موفقیت تغییر یافت");

                ApiResponse response = ApiResponse.success(data);
                sendJsonResponse(exchange, 200, response.toMap());
            } else {
                ApiResponse response = ApiResponse.error("دانشجو یافت نشد");
                sendJsonResponse(exchange, 404, response.toMap());
            }
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleStudentBorrowHistory(HttpExchange exchange, String username) throws IOException {
        if (!checkAuthorization(exchange, "EMPLOYEE")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        List<Loan> history = libraryManager.getStudentLoanHistory(username);

        List<Map<String, Object>> historyData = history.stream()
                .map(this::convertLoanToMap)
                .collect(Collectors.toList());

        ApiResponse response = ApiResponse.success(historyData);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleStatsSummary(HttpExchange exchange) throws IOException {
        Map<String, Object> stats = new HashMap<>();

        stats.put("totalStudents", libraryManager.getTotalStudents());
        stats.put("activeStudents", libraryManager.getActiveStudents());
        stats.put("totalBooks", libraryManager.getTotalBooks());
        stats.put("availableBooks", libraryManager.getAvailableBooks());
        stats.put("totalLoans", libraryManager.getTotalLoans());
        stats.put("currentLoans", libraryManager.getCurrentLoans());
        stats.put("pendingLoans", libraryManager.getPendingLoans().size());
        stats.put("overdueLoans", libraryManager.getOverdueLoans().size());

        ApiResponse response = ApiResponse.success(stats);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleBorrowStats(HttpExchange exchange) throws IOException {
        if (!checkAuthorization(exchange, "MANAGER")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        LoanStatistics stats = libraryManager.getLoanStatistics();

        Map<String, Object> data = new HashMap<>();
        data.put("totalRequests", stats.getTotalRequests());
        data.put("totalApproved", stats.getTotalApproved());
        data.put("averageLoanDays", stats.getAverageLoanDays());
        if (stats.getTotalRequests() > 0) {
            data.put("approvalRate", (double) stats.getTotalApproved() / stats.getTotalRequests() * 100);
        } else {
            data.put("approvalRate", 0);
        }

        ApiResponse response = ApiResponse.success(data);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleEmployeePerformance(HttpExchange exchange, String employeeUsername) throws IOException {
        if (!checkAuthorization(exchange, "MANAGER")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        EmployeeStatistics stats = libraryManager.getEmployeeStatistics(employeeUsername);

        Map<String, Object> data = new HashMap<>();
        data.put("employeeUsername", employeeUsername);
        data.put("booksRegistered", stats.getBooksRegistered());
        data.put("loansApproved", stats.getLoansApproved());
        data.put("returnsProcessed", stats.getReturnsProcessed());
        data.put("totalActions", stats.getBooksRegistered() + stats.getLoansApproved() + stats.getReturnsProcessed());

        ApiResponse response = ApiResponse.success(data);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleTopDelayed(HttpExchange exchange) throws IOException {
        if (!checkAuthorization(exchange, "MANAGER")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        List<Student> delayedStudents = libraryManager.getTopDelayedStudents();

        List<Map<String, Object>> studentsData = delayedStudents.stream()
                .map(student -> {
                    Map<String, Object> data = convertStudentToMap(student);
                    StudentStatistics stats = libraryManager.getStudentStatistics(student.getUsername());
                    data.put("delayedReturns", stats.getDelayedReturns());
                    data.put("totalLoans", stats.getTotalLoans());
                    return data;
                })
                .collect(Collectors.toList());

        ApiResponse response = ApiResponse.success(studentsData);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    private void handleCreateEmployee(HttpExchange exchange) throws IOException {
        if (!checkAuthorization(exchange, "MANAGER")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        try {
            Map<String, String> request = parseJsonRequest(exchange);

            if (request.get("username") == null || request.get("password") == null ||
                    request.get("employeeId") == null || request.get("name") == null) {
                ApiResponse response = ApiResponse.error("مقادیر اجباری ارسال نشده‌اند");
                sendJsonResponse(exchange, 400, response.toMap());
                return;
            }

            Employee employee = new Employee(
                    request.get("username"),
                    request.get("password"),
                    request.get("employeeId"),
                    request.get("name"));

            boolean success = libraryManager.addEmployee(employee);

            ApiResponse response = success
                    ? ApiResponse.success("کارمند با موفقیت ایجاد شد", convertEmployeeToMap(employee))
                    : ApiResponse.error("نام کاربری یا شناسه کارمند تکراری است");

            sendJsonResponse(exchange, success ? 201 : 400, response.toMap());
        } catch (Exception e) {
            sendError(exchange, 400, "Invalid request body");
        }
    }

    private void handleGetEmployees(HttpExchange exchange) throws IOException {
        if (!checkAuthorization(exchange, "MANAGER")) {
            sendError(exchange, 403, "Forbidden");
            return;
        }

        List<Employee> employees = libraryManager.getEmployees();

        List<Map<String, Object>> employeesData = employees.stream()
                .map(this::convertEmployeeToMap)
                .collect(Collectors.toList());

        ApiResponse response = ApiResponse.success(employeesData);
        sendJsonResponse(exchange, 200, response.toMap());
    }

    // ==================== Helper Methods ====================

    private Map<String, Object> convertBookToMap(Book book) {
        Map<String, Object> map = new HashMap<>();
        map.put("bookId", book.getBookId());
        map.put("title", book.getTitle());
        map.put("author", book.getAuthor());
        map.put("publisher", book.getPublisher());
        map.put("publicationYear", book.getPublicationYear());
        map.put("isbn", book.getIsbn());
        map.put("available", book.isAvailable());
        return map;
    }

    private Map<String, Object> convertLoanToMap(Loan loan) {
        Map<String, Object> map = new HashMap<>();
        map.put("loanId", loan.getLoanId());
        map.put("studentUsername", loan.getStudentUsername());
        map.put("bookId", loan.getBookId());
        map.put("startDate", loan.getStartDate().toString());
        map.put("endDate", loan.getEndDate().toString());
        map.put("actualReturnDate", loan.getActualReturnDate() != null ? loan.getActualReturnDate().toString() : null);
        map.put("status", loan.getStatus());
        map.put("overdue", loan.isOverdue());
        map.put("overdueDays", loan.getOverdueDays());
        map.put("employeeUsername", loan.getEmployeeUsername());
        return map;
    }

    private Map<String, Object> convertStudentToMap(Student student) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", student.getUsername());
        map.put("studentId", student.getStudentId());
        map.put("name", student.getName());
        map.put("email", student.getEmail());
        map.put("active", student.isActive());
        map.put("role", student.getRole());
        return map;
    }

    private Map<String, Object> convertEmployeeToMap(Employee employee) {
        Map<String, Object> map = new HashMap<>();
        map.put("username", employee.getUsername());
        map.put("employeeId", employee.getEmployeeId());
        map.put("name", employee.getName());
        map.put("active", employee.isActive());
        map.put("role", employee.getRole());
        return map;
    }

    private String getFullName(User user, String role) {
        if ("STUDENT".equals(role)) {
            return ((Student) user).getName();
        } else {
            return ((Employee) user).getName();
        }
    }

    // ==================== Authentication Helpers ====================

    private boolean checkAuthorization(HttpExchange exchange, String requiredRole) {
        String token = getAuthToken(exchange);
        if (!isAuthenticated(token))
            return false;

        String role = getRoleFromToken(token);
        if (role == null)
            return false;

        switch (requiredRole) {
            case "STUDENT":
                return "STUDENT".equals(role) || "EMPLOYEE".equals(role) || "MANAGER".equals(role);
            case "EMPLOYEE":
                return "EMPLOYEE".equals(role) || "MANAGER".equals(role);
            case "MANAGER":
                return "MANAGER".equals(role);
            default:
                return false;
        }
    }

    private String getAuthToken(HttpExchange exchange) {
        String authHeader = exchange.getRequestHeaders().getFirst("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    private boolean isAuthenticated(String token) {
        return token != null && activeTokens.containsKey(token);
    }

    private String getUserFromToken(String token) {
        String userRole = activeTokens.get(token);
        if (userRole != null && userRole.contains(":")) {
            return userRole.split(":")[0];
        }
        return null;
    }

    private String getRoleFromToken(String token) {
        String userRole = activeTokens.get(token);
        if (userRole != null && userRole.contains(":")) {
            return userRole.split(":")[1];
        }
        return null;
    }

    // ==================== JSON Processing ====================

    private Map<String, String> parseJsonRequest(HttpExchange exchange) throws IOException {
        String requestBody = readRequestBody(exchange);
        return parseJson(requestBody);
    }

    private Map<String, String> parseJson(String json) {
        Map<String, String> result = new HashMap<>();
        if (json == null || json.trim().isEmpty())
            return result;

        json = json.trim();
        if (json.startsWith("{") && json.endsWith("}")) {
            json = json.substring(1, json.length() - 1);
            String[] pairs = json.split(",");

            for (String pair : pairs) {
                String[] keyValue = pair.split(":", 2);
                if (keyValue.length == 2) {
                    String key = keyValue[0].trim().replace("\"", "");
                    String value = keyValue[1].trim().replace("\"", "");
                    result.put(key, value);
                }
            }
        }
        return result;
    }

    private Map<String, String> parseQueryParams(HttpExchange exchange) {
        Map<String, String> params = new HashMap<>();
        String query = exchange.getRequestURI().getQuery();
        if (query != null) {
            for (String param : query.split("&")) {
                String[] pair = param.split("=");
                if (pair.length > 1) {
                    params.put(pair[0], pair[1]);
                } else if (pair.length == 1) {
                    params.put(pair[0], "");
                }
            }
        }
        return params;
    }

    private String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream is = exchange.getRequestBody();
                ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
            return bos.toString(StandardCharsets.UTF_8);
        }
    }

    private void sendJsonResponse(HttpExchange exchange, int statusCode, Object data) throws IOException {
        String jsonResponse = mapToJson(data);

        exchange.getResponseHeaders().set("Content-Type", "application/json; charset=UTF-8");
        exchange.getResponseHeaders().set("Access-Control-Allow-Origin", "*");

        byte[] responseBytes = jsonResponse.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(statusCode, responseBytes.length);

        try (OutputStream os = exchange.getResponseBody()) {
            os.write(responseBytes);
        }
    }

    private void sendError(HttpExchange exchange, int statusCode, String message) throws IOException {
        ApiResponse response = ApiResponse.error(message);
        sendJsonResponse(exchange, statusCode, response.toMap());
    }

    private String mapToJson(Object obj) {
        if (obj instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) obj;
            StringBuilder json = new StringBuilder("{");
            boolean first = true;

            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first)
                    json.append(",");
                first = false;

                json.append("\"").append(entry.getKey()).append("\":");

                Object value = entry.getValue();
                if (value == null) {
                    json.append("null");
                } else if (value instanceof String) {
                    json.append("\"").append(escapeJson(value.toString())).append("\"");
                } else if (value instanceof Number || value instanceof Boolean) {
                    json.append(value);
                } else if (value instanceof Map || value instanceof List) {
                    json.append(mapToJson(value));
                } else {
                    json.append("\"").append(escapeJson(value.toString())).append("\"");
                }
            }
            json.append("}");
            return json.toString();
        } else if (obj instanceof List) {
            List<?> list = (List<?>) obj;
            StringBuilder json = new StringBuilder("[");
            boolean first = true;

            for (Object item : list) {
                if (!first)
                    json.append(",");
                first = false;

                if (item == null) {
                    json.append("null");
                } else if (item instanceof Map || item instanceof List) {
                    json.append(mapToJson(item));
                } else if (item instanceof String) {
                    json.append("\"").append(escapeJson(item.toString())).append("\"");
                } else {
                    json.append(item.toString());
                }
            }
            json.append("]");
            return json.toString();
        }
        return "{}";
    }

    private String escapeJson(String str) {
        if (str == null)
            return "";
        return str.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
package ir.university.library.model;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class Loan implements Serializable {
    private String loanId;
    private String studentUsername;
    private String bookId;
    private String employeeUsername;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate actualReturnDate;
    private String status; // PENDING, APPROVED, RETURNED, OVERDUE

    public Loan(String loanId, String studentUsername, String bookId,
            LocalDate startDate, LocalDate endDate) {
        this.loanId = loanId;
        this.studentUsername = studentUsername;
        this.bookId = bookId;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = "PENDING";
    }

    // Getters and Setters
    public String getLoanId() {
        return loanId;
    }

    public void setLoanId(String loanId) {
        this.loanId = loanId;
    }

    public String getStudentUsername() {
        return studentUsername;
    }

    public void setStudentUsername(String studentUsername) {
        this.studentUsername = studentUsername;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getEmployeeUsername() {
        return employeeUsername;
    }

    public void setEmployeeUsername(String employeeUsername) {
        this.employeeUsername = employeeUsername;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public LocalDate getActualReturnDate() {
        return actualReturnDate;
    }

    public void setActualReturnDate(LocalDate actualReturnDate) {
        this.actualReturnDate = actualReturnDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isOverdue() {
        if ("RETURNED".equals(status) && actualReturnDate != null) {
            return actualReturnDate.isAfter(endDate);
        } else if ("APPROVED".equals(status)) {
            return LocalDate.now().isAfter(endDate);
        }
        return false;
    }

    public long getOverdueDays() {
        if (isOverdue()) {
            if ("RETURNED".equals(status) && actualReturnDate != null) {
                return ChronoUnit.DAYS.between(endDate, actualReturnDate);
            } else if ("APPROVED".equals(status)) {
                return ChronoUnit.DAYS.between(endDate, LocalDate.now());
            }
        }
        return 0;
    }

    @Override
    public String toString() {
        return "Loan{loanId='" + loanId + "', student='" + studentUsername +
                "', book='" + bookId + "', employee='" + employeeUsername +
                "', start=" + startDate + ", end=" + endDate +
                ", return=" + actualReturnDate + ", status='" + status + "'}";
    }
}
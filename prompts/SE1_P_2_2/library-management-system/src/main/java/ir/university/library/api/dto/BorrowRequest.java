package ir.university.library.api.dto;

import java.time.LocalDate;

public class BorrowRequest {
    private String bookId;
    private LocalDate startDate;
    private LocalDate endDate;

    public BorrowRequest() {
    }

    public BorrowRequest(String bookId, LocalDate startDate, LocalDate endDate) {
        this.bookId = bookId;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
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
}
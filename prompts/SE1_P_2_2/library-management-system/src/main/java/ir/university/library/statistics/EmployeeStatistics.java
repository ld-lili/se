package ir.university.library.statistics;

public class EmployeeStatistics {
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
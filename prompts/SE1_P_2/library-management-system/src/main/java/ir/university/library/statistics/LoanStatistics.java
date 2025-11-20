package ir.university.library.statistics;

public class LoanStatistics {
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
package ir.university.library.statistics;

public class StudentStatistics {
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
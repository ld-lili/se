package ir.university.library.exception;

public class InvalidStudentStatusException extends RuntimeException {
    public InvalidStudentStatusException(String message) {
        super(message);
    }
}
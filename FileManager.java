import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileManager {
    private static final String STUDENTS_FILE = "students.dat";
    private static final String EMPLOYEES_FILE = "employees.dat";
    private static final String BOOKS_FILE = "books.dat";
    private static final String LOANS_FILE = "loans.dat";

    public static void saveStudents(List<Student> students) {
        saveToFile(students, STUDENTS_FILE);
    }

    public static void saveEmployees(List<Employee> employees) {
        saveToFile(employees, EMPLOYEES_FILE);
    }

    public static void saveBooks(List<Book> books) {
        saveToFile(books, BOOKS_FILE);
    }

    public static void saveLoans(List<Loan> loans) {
        saveToFile(loans, LOANS_FILE);
    }

    @SuppressWarnings("unchecked")
    public static List<Student> loadStudents() {
        Object result = loadFromFile(STUDENTS_FILE);
        if (result instanceof List) {
            return (List<Student>) result;
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static List<Employee> loadEmployees() {
        Object result = loadFromFile(EMPLOYEES_FILE);
        if (result instanceof List) {
            return (List<Employee>) result;
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static List<Book> loadBooks() {
        Object result = loadFromFile(BOOKS_FILE);
        if (result instanceof List) {
            return (List<Book>) result;
        }
        return new ArrayList<>();
    }

    @SuppressWarnings("unchecked")
    public static List<Loan> loadLoans() {
        Object result = loadFromFile(LOANS_FILE);
        if (result instanceof List) {
            return (List<Loan>) result;
        }
        return new ArrayList<>();
    }

    private static void saveToFile(Object obj, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(obj);
        } catch (IOException e) {
            System.out.println("Error saving to " + filename + ": " + e.getMessage());
        }
    }

    private static Object loadFromFile(String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            return new ArrayList<>();
        }

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading from " + filename + ": " + e.getMessage());
            return new ArrayList<>();
        }
    }
}
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.stream.*;

// Data class representing a student
class Student {
    private String id;
    private String name;
    private List<Integer> grades;

    public Student(String id, String name, List<Integer> grades) {
        this.id = id;
        this.name = name;
        this.grades = grades;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public List<Integer> getGrades() { return grades; }

    public double getAverageGrade() {
        return grades.stream()
                     .mapToInt(Integer::intValue)
                     .average()
                     .orElse(0.0);
    }

    @Override
    public String toString() {
        return String.format("%s (%s): Avg Grade = %.2f", name, id, getAverageGrade());
    }
}

// Custom exception for invalid student data
class InvalidStudentDataException extends Exception {
    public InvalidStudentDataException(String message) {
        super(message);
    }
}

// Generic repository to manage any type of entity
class Repository<T> {
    private List<T> items = new ArrayList<>();

    public void add(T item) { items.add(item); }
    public List<T> getAll() { return Collections.unmodifiableList(items); }

    public void forEach(Consumer<T> action) {
        for (T item : items) action.accept(item);
    }

    public List<T> filter(Predicate<T> predicate) {
        return items.stream().filter(predicate).collect(Collectors.toList());
    }
}

// Main program
public class StudentManagementSystem {

    // Reads students from a text file
    private static List<Student> readStudents(String filePath) throws IOException, InvalidStudentDataException {
        List<String> lines = Files.readAllLines(Paths.get(filePath));
        List<Student> students = new ArrayList<>();

        for (String line : lines) {
            String[] parts = line.split(",");
            if (parts.length < 3) {
                throw new InvalidStudentDataException("Invalid data: " + line);
            }

            String id = parts[0].trim();
            String name = parts[1].trim();

            List<Integer> grades = new ArrayList<>();
            for (int i = 2; i < parts.length; i++) {
                try {
                    grades.add(Integer.parseInt(parts[i].trim()));
                } catch (NumberFormatException e) {
                    throw new InvalidStudentDataException("Invalid grade for " + name + ": " + parts[i]);
                }
            }

            students.add(new Student(id, name, grades));
        }

        return students;
    }

    // Display statistics using streams
    private static void displayStatistics(List<Student> students) {
        System.out.println("\n=== Student Statistics ===");

        // Average of all students
        double overallAvg = students.stream()
                                    .mapToDouble(Student::getAverageGrade)
                                    .average()
                                    .orElse(0.0);
        System.out.printf("Overall Average Grade: %.2f%n", overallAvg);

        // Top performer
        students.stream()
                .max(Comparator.comparingDouble(Student::getAverageGrade))
                .ifPresent(top -> System.out.println("Top Performer: " + top));

        // Students above average
        System.out.println("\nStudents above average:");
        students.stream()
                .filter(s -> s.getAverageGrade() > overallAvg)
                .forEach(System.out::println);
    }

    public static void main(String[] args) {
        System.out.println("=== Student Management System ===");

        String filePath = "students.txt"; // Example input file

        try {
            List<Student> students = readStudents(filePath);
            Repository<Student> repo = new Repository<>();
            students.forEach(repo::add);

            System.out.println("\nAll Students:");
            repo.forEach(System.out::println);

            displayStatistics(repo.getAll());

        } catch (FileNotFoundException e) {
            System.err.println("Error: File not found.");
        } catch (InvalidStudentDataException e) {
            System.err.println("Error: " + e.getMessage());
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }
}

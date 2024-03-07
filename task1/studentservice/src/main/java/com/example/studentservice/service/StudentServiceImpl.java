package com.example.studentservice.service;

import com.example.studentservice.model.Student;
import com.example.studentservice.model.ResponseWrapper;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Service
public class StudentServiceImpl implements StudentService {

    private final List<Student> students = new ArrayList<>();
    private Long manualStudentIdCounter = 4L;
    private final Set<Long> existingIds = new HashSet<>();

    // Initialize with sample data
    {
        students.add(new Student(1L, "Alice", 22, "Engineering", "admin" ));
        students.add(new Student(2L, "Bob", 21, "Mathematics", "client"));
        students.add(new Student(3L, "Charlie", 23, "History", "client"));

        System.out.println("Initial List size: " + students.size());
    }


    @Override
    public ResponseWrapper<List<Student>> getAllStudents() {
        System.out.println("List size: " + students.size());
        Long manualStudentIdCounter = 1L;
        return createResponse(students, "Successfully retrieved students");
    }

    @Override
    public List<Student> getData() {
        return getAllStudentsInternals();
    }


    private List<Student> getAllStudentsInternals() {

        return new ArrayList<>(students);
    }


    private ResponseWrapper<List<Student>> createResponse(List<Student> students, String successfullyRetrievedStudents) {
        ResponseWrapper<List<Student>> response = new ResponseWrapper<>();
        try {
            System.out.println("JIO");
            response.setData(students);
            response.setMessage(successfullyRetrievedStudents);
            response.setStatusCode(200);
        } catch (Exception e) {
            response.setError(true);
            response.setMessage("Error: " + e.getMessage());
            response.setStatusCode(500);
        }
        return response;
    }


    @Override
    public ResponseWrapper<Student> getStudentById(Long studentId) {
        ResponseWrapper<Student> response = new ResponseWrapper<>();
        try {
            // Find the student with the specified ID
            Student foundStudent = students.stream()
                    .filter(student -> student.getId().equals(studentId))
                    .findFirst()
                    .orElse(null);

            if (foundStudent != null) {
                response.setData(foundStudent);
                response.setMessage("Student found successfully");
                response.setStatusCode(200);
            } else {
                response.setError(true);
                response.setMessage("Student not found");
                response.setStatusCode(404);
            }
        } catch (Exception e) {
            response.setError(true);
            response.setMessage("Error retrieving student: " + e.getMessage());
            response.setStatusCode(500);
        }
        return response;
    }

    @Override
    public ResponseWrapper<Student> createStudent(Student student) {
        ResponseWrapper<Student> response = new ResponseWrapper<>();
        try {
            Long providedStudentId = student.getId();

            // Check if the provided ID already exists in the set
            if (existingIds.contains(providedStudentId)) {
                response.setError(true);
                response.setMessage("Provided Student ID already exists. Please choose a different ID.");
            } else {
                // Check if a student with the same data already exists
                if (isDuplicateStudentData(student)) {
                    response.setError(true);
                    response.setMessage("A student with the same data already exists. Please provide unique data.");
                } else {
                    students.add(student);
                    existingIds.add(providedStudentId);

                    response.setData(student);
                    response.setMessage("Student created successfully");
                    response.setStatusCode(201);

                    // Get the updated list after creating a student
                    List<Student> updatedStudentList = getAllStudentsInternals();
                    response.setAdditionalData(updatedStudentList);
                }
            }
        } catch (Exception e) {
            response.setError(true);
            response.setMessage("Error creating student: " + e.getMessage());
            response.setStatusCode(500);
        }
        return response;
    }

    @Override
    public ResponseWrapper<Student> updateStudent(Long studentId, Student updatedStudent, String userRole) {
        return null;
    }
    // Method to check if a student with the same data already exists
    private boolean isDuplicateStudentData(Student newStudent) {
        return students.stream()
                .anyMatch(existingStudent ->
                        existingStudent.getName().equals(newStudent.getName()) &&
                                existingStudent.getAge() == newStudent.getAge() &&
                                existingStudent.getId() == newStudent.getId() &&
                                existingStudent.getMajor().equals(newStudent.getMajor()) &&
                                existingStudent.getRole().equals(newStudent.getRole()));
    }
    @Override
    public ResponseWrapper<List<Student>> updateStudent(@PathVariable Long studentId, @RequestBody Student updatedStudent) {
        ResponseWrapper<List<Student>> response = new ResponseWrapper<>();
        try {
            Student existingStudent = students.stream()
                    .filter(student -> student.getId().equals(studentId))
                    .findFirst()
                    .orElse(null);

            // Check if the student exists
            if (existingStudent != null) {
                // Check if the user has admin role
                if ("admin".equals(existingStudent.getRole())) {
                    Student abhi = students.stream()
                            .filter(student -> student.getId().equals(updatedStudent.getId()))
                            .findFirst()
                            .orElse(null);
                    // Allow admin to update data for any student
                    abhi.setName(updatedStudent.getName());
                    abhi.setAge(updatedStudent.getAge());
                    abhi.setMajor(updatedStudent.getMajor());

                    response.setData(students);
                    response.setMessage("Student updated successfully");
                    response.setStatusCode(200);

                } else {
                    response.setError(true);
                    response.setMessage("User does not have permission to update this student");
                    response.setStatusCode(403); // Forbidden
                }
            } else {
                response.setError(true);
                response.setMessage("Student not found for update");
                response.setStatusCode(404);
            }
        } catch (Exception e) {
            response.setError(true);
            response.setMessage("Error updating student: " + e.getMessage());
            response.setStatusCode(500);
        }
        return response;
    }
    @Override
    public ResponseWrapper<Boolean> deleteStudent(Long studentId) {
        ResponseWrapper<Boolean> response = new ResponseWrapper<>();
        try {
            // Remove the student with the specified ID
            boolean removed = students.removeIf(student -> student.getId().equals(studentId));

            if (removed) {
                response.setData(true);
                response.setMessage("Student deleted successfully");
                response.setStatusCode(200);
            } else {
                response.setData(false);
                response.setMessage("Student not found for deletion");
                response.setStatusCode(404);
            }


            List<Student> updatedStudentList = getAllStudentsInternals();
            response.setAdditionalData(students);

        } catch (Exception e) {
            response.setError(true);
            response.setMessage("Error deleting student: " + e.getMessage());
            response.setStatusCode(500);
        }
        return response;
    }

    private Long generateNewStudentId() {
        return System.currentTimeMillis();
    }
}
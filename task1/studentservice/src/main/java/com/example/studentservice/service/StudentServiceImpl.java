package com.example.studentservice.service;

import com.example.studentservice.model.Student;
import com.example.studentservice.model.ResponseWrapper;
import org.springframework.stereotype.Service;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class StudentServiceImpl implements StudentService {

    private final List<Student> students = new ArrayList<>();
    private final Set<Long> existingIds = new HashSet<>();

    // Initialize with sample data
    {
        students.add(new Student(1L, "Alice", 22, "Engineering", "admin"));
        students.add(new Student(2L, "Bob", 21, "Mathematics", "client"));
        students.add(new Student(3L, "Charlie", 23, "History", "client"));
    }

    @Override
    public ResponseWrapper<List<Student>> getAllStudents() {
        return createResponse(students, "Successfully retrieved students");
    }

    @Override
    public List<Student> getData() {
        return getAllStudentsInternals();
    }

    private List<Student> getAllStudentsInternals() {
        return new ArrayList<>(students);
    }

    @Override
    public ResponseWrapper<Student> getStudentById(Long studentId) {
        ResponseWrapper<Student> response = new ResponseWrapper<>();
        try {
            // Find the student with the specified ID
            Student foundStudent = findStudentById(studentId);

            if (foundStudent != null) {
                response.setData(foundStudent);
                response.setMessage("Student found successfully");
            } else {
                response.setError(true);
                response.setMessage("Student not found");
                response.setStatusCode(404);
            }
        } catch (Exception e) {
            handleErrorResponse(response, e);
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
                    // condition for admin
                    if ( "admin".equals(student.getRole())) {
                        students.add(student);
                        existingIds.add(providedStudentId);

                        response.setData(student);
                        response.setMessage("Student created successfully");
                        response.setStatusCode(201);
                        response.setAdditionalData(getAllStudentsList());
                    } else {
                        response.setError(true);
                        response.setMessage("Only admins and admins can create a new student");
                        response.setStatusCode(403); // Forbidden
                    }
                }
            }
        } catch (Exception e) {
            handleErrorResponse(response, e);
        }
        return response;
    }

    @Override
    public ResponseWrapper<Boolean> deleteStudent(Long studentId) {
        ResponseWrapper<Boolean> response = new ResponseWrapper<>();
        try {
            // Find the student with the specified ID
            Student studentToDelete = findStudentById(studentId);

            // Check if the student exists
            if (studentToDelete != null) {
                // Check if the user has admin role
                if ("admin".equals(studentToDelete.getRole())) {
                    response   .setData(false);
                    response.setMessage("User with role 'admin' cannot be deleted");
                    response.setStatusCode(403); // Forbidden
                } else

                if (!"client".equals(studentToDelete.getRole())){
                    response.setData(false);
                    response.setMessage("Student not found for deletion");
                    response.setStatusCode(404);

                    // Remove the student with the specified ID
                    boolean removed = students.removeIf(student -> student.getId().equals(studentId));

                    if (removed) {
                        response.setData(true);
                        response.setMessage("Student deleted successfully");
                    }

                    response.setAdditionalData(getAllStudentsList());
                }
            } else {
                response.setError(true);
                response.setMessage("Student not found for deletion");
                response.setStatusCode(404);
            }
        } catch (Exception e) {
            handleErrorResponse(response, e);
        }
        return response;
    }

    private Student findStudentById(Long studentId) {
        return students.stream()
                .filter(student -> student.getId().equals(studentId))
                .findFirst()
                .orElse(null);
    }

    private boolean isDuplicateStudentData(Student newStudent) {
        return students.stream()
                .anyMatch(existingStudent ->
                        existingStudent.getId().equals(newStudent.getId()) &&
                                existingStudent.getName().equals(newStudent.getName()) &&
                                existingStudent.getAge() == newStudent.getAge() &&
                                existingStudent.getMajor().equals(newStudent.getMajor()) &&
                                existingStudent.getRole().equals(newStudent.getRole()));
    }
    @Override
    public ResponseWrapper<List<Student>> updateStudent( Long studentId,  Student updatedStudent) {
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
    public ResponseWrapper<Student> updateStudent(Long studentId, Student updatedStudent, String userRole) {
        return null;
    }

    private List<Student> getAllStudentsList() {
        return new ArrayList<>(students);
    }

    private <T> ResponseWrapper<T> createResponse(T data, String message) {
        ResponseWrapper<T> response = new ResponseWrapper<>();
        response.setData(data);
        response.setMessage(message);
        response.setStatusCode(200);
        return response;
    }

    private <T> void handleErrorResponse(ResponseWrapper<T> response, Exception e) {
        response.setError(true);
        response.setMessage("Error: " + e.getMessage());
        response.setStatusCode(500);
    }
}

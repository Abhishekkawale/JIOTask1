package com.example.studentservice.controller;

import com.example.studentservice.model.Student;
import com.example.studentservice.service.StudentService;
import com.example.studentservice.model.ResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping
    public ResponseWrapper<List<Student>> getAllStudents() {
        return studentService.getAllStudents();
    }





    @PostMapping
    public ResponseWrapper<Student> createStudent(@RequestBody Student student) {
        System.out.println(student.getId()+ " , " + student.getName()+ " , " + student.getAge()+ " , " + student.getMajor()+ " , ");
                return studentService.createStudent(student);
    }

    @DeleteMapping("/{studentId}")
    public ResponseWrapper<Boolean> deleteStudent(@PathVariable Long studentId) {
        return studentService.deleteStudent(studentId);
    }

    @GetMapping("/{studentId}")
    public ResponseWrapper<Student> getStudentById(@PathVariable Long studentId) {
        return studentService.getStudentById(studentId);
    }

    @PutMapping("/{studentId}")
    public ResponseWrapper<List<Student>> updateStudent(
            @PathVariable Long studentId,
            @RequestBody Student updatedStudent) {
        return studentService.updateStudent(studentId, updatedStudent);

    }


}

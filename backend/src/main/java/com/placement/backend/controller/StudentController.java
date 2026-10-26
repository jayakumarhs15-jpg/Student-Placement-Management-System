package com.placement.backend.controller;

import com.placement.backend.entity.Student;
import com.placement.backend.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("/{id}")
    public Student getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Student createStudent(@Valid @RequestBody Student student) {

        student.setId(null);

        if (studentRepository.existsByEmailIgnoreCase(student.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A student with this email already exists");
        }

        return studentRepository.save(student);
    }
}
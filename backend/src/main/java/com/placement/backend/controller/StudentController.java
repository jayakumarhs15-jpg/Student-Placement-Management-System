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

    @PutMapping("/{id}")
    public Student updateStudent(
            @PathVariable Long id,
            @Valid @RequestBody Student updatedStudent) {

        Student existingStudent = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        if (!existingStudent.getEmail().equalsIgnoreCase(updatedStudent.getEmail())
                && studentRepository.existsByEmailIgnoreCase(updatedStudent.getEmail())) {
            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "A student with this email already exists");
        }

        existingStudent.setName(updatedStudent.getName());
        existingStudent.setEmail(updatedStudent.getEmail());
        existingStudent.setDepartment(updatedStudent.getDepartment());
        existingStudent.setCgpa(updatedStudent.getCgpa());
        existingStudent.setGraduationYear(updatedStudent.getGraduationYear());

        return studentRepository.save(existingStudent);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteStudent(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        studentRepository.delete(student);
    }
}
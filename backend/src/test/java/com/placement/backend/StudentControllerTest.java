
package com.placement.backend;

import com.placement.backend.controller.StudentController;
import com.placement.backend.entity.Student;
import com.placement.backend.repository.StudentRepository;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StudentController.class)
class StudentControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private StudentRepository studentRepository;

    // Test 1: Verify that the API returns an empty student list.
    @Test
    void getStudentsShouldReturnOk() throws Exception {
        when(studentRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    // Test 2: Verify that a missing student returns HTTP 404.
    @Test
    void getStudentShouldReturnNotFoundWhenMissing() throws Exception {
        when(studentRepository.findById(999L))
                .thenReturn(Optional.empty());

        mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound());
    }

    // Test 3: Verify that duplicate student emails are rejected.
    @Test
    void createStudentShouldRejectDuplicateEmail() throws Exception {
        when(studentRepository.existsByEmailIgnoreCase("arun@example.com"))
                .thenReturn(true);

        String studentJson = """
                {
                    "name": "Arun Kumar",
                    "email": "arun@example.com",
                    "department": "Computer Science",
                    "cgpa": 8.5,
                    "graduationYear": "2027"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isConflict());

        verify(studentRepository, never()).save(any(Student.class));
    }

    // Test 4: Verify updating an existing student
    @Test
    void updateStudentShouldUpdateAndReturn() throws Exception {
        Student existing = new Student();
        existing.setId(1L);
        existing.setName("Old Name");
        existing.setEmail("old@example.com");
        existing.setDepartment("IT");
        existing.setCgpa(8.0);
        existing.setGraduationYear("2026");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        String updateJson = """
                {
                    "name": "New Name",
                    "email": "old@example.com",
                    "department": "CSE",
                    "cgpa": 9.0,
                    "graduationYear": "2026"
                }
                """;

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("New Name"))
                .andExpect(jsonPath("$.department").value("CSE"))
                .andExpect(jsonPath("$.cgpa").value(9.0));
    }

    // Test 5: Verify deleting a student returns 204 No Content
    @Test
    void deleteStudentShouldReturnNoContent() throws Exception {
        Student existing = new Student();
        existing.setId(1L);

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isNoContent());

        verify(studentRepository, times(1)).delete(existing);
    }

    // Test 6: Verify deleting a missing student returns 404
    @Test
    void deleteStudentShouldReturnNotFoundWhenMissing() throws Exception {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/students/999"))
                .andExpect(status().isNotFound());
    }

    // Test 7: Verify creating a student successfully returns 201 Created
    @Test
    void createStudentShouldSaveAndReturn() throws Exception {
        when(studentRepository.existsByEmailIgnoreCase("new@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> {
            Student s = i.getArgument(0);
            s.setId(10L);
            return s;
        });

        String studentJson = """
                {
                    "name": "New Student",
                    "email": "new@example.com",
                    "department": "Mechanical",
                    "cgpa": 8.2,
                    "graduationYear": "2026"
                }
                """;

        mockMvc.perform(post("/api/students")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(studentJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("New Student"));
    }

    // Test 8: Verify update student when missing returns 404
    @Test
    void updateStudentShouldReturnNotFoundWhenMissing() throws Exception {
        when(studentRepository.findById(999L)).thenReturn(Optional.empty());

        String updateJson = """
                {
                    "name": "Missing",
                    "email": "missing@example.com",
                    "department": "CSE",
                    "cgpa": 7.5,
                    "graduationYear": "2026"
                }
                """;

        mockMvc.perform(put("/api/students/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isNotFound());
    }

    // Test 9: Verify update student with conflicting new email returns 409
    @Test
    void updateStudentShouldRejectDuplicateEmailOnEmailChange() throws Exception {
        Student existing = new Student();
        existing.setId(1L);
        existing.setName("Existing");
        existing.setEmail("first@example.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailIgnoreCase("second@example.com")).thenReturn(true);

        String updateJson = """
                {
                    "name": "Existing",
                    "email": "second@example.com",
                    "department": "CSE",
                    "cgpa": 8.0,
                    "graduationYear": "2026"
                }
                """;

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isConflict());
    }

    // Test 10: Verify update student with non-conflicting new email succeeds
    @Test
    void updateStudentShouldSucceedWithNonConflictingNewEmail() throws Exception {
        Student existing = new Student();
        existing.setId(1L);
        existing.setName("Existing");
        existing.setEmail("old@example.com");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(studentRepository.existsByEmailIgnoreCase("brandnew@example.com")).thenReturn(false);
        when(studentRepository.save(any(Student.class))).thenAnswer(i -> i.getArgument(0));

        String updateJson = """
                {
                    "name": "Existing Updated",
                    "email": "brandnew@example.com",
                    "department": "ECE",
                    "cgpa": 8.8,
                    "graduationYear": "2027"
                }
                """;

        mockMvc.perform(put("/api/students/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("brandnew@example.com"));
    }
}

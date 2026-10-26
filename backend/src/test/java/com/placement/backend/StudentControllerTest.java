
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
}

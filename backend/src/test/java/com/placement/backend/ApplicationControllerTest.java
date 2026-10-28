package com.placement.backend;

import com.placement.backend.controller.ApplicationController;
import com.placement.backend.entity.Application;
import com.placement.backend.entity.PlacementDrive;
import com.placement.backend.entity.Student;
import com.placement.backend.repository.ApplicationRepository;
import com.placement.backend.repository.PlacementDriveRepository;
import com.placement.backend.repository.StudentRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ApplicationController.class)
class ApplicationControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ApplicationRepository applicationRepository;

    @MockitoBean
    private StudentRepository studentRepository;

    @MockitoBean
    private PlacementDriveRepository placementDriveRepository;

    @Test
    void getAllApplicationsShouldReturnOk() throws Exception {
        when(applicationRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/applications"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getApplicationByIdShouldReturnWhenFound() throws Exception {
        Application app = new Application();
        app.setId(1L);
        app.setStudentId(10L);
        app.setDriveId(20L);
        app.setStatus("APPLIED");
        app.setAppliedAt(LocalDateTime.now());

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        mockMvc.perform(get("/api/applications/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.studentId").value(10))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void getApplicationByIdShouldReturnNotFound() throws Exception {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/applications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createApplicationShouldSucceedWhenEligible() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setCgpa(8.5);

        PlacementDrive drive = new PlacementDrive();
        drive.setId(2L);
        drive.setMinimumCgpa(7.5);
        drive.setApplicationDeadline("2026-12-31");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(2L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentIdAndDriveId(1L, 2L)).thenReturn(false);
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> {
            Application a = i.getArgument(0);
            a.setId(100L);
            return a;
        });

        String requestJson = """
                {
                    "studentId": 1,
                    "driveId": 2
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.status").value("APPLIED"));
    }

    @Test
    void createApplicationShouldRejectWhenIneligibleCgpa() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setCgpa(6.5);

        PlacementDrive drive = new PlacementDrive();
        drive.setId(2L);
        drive.setMinimumCgpa(7.5);
        drive.setApplicationDeadline("2026-12-31");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(2L)).thenReturn(Optional.of(drive));

        String requestJson = """
                {
                    "studentId": 1,
                    "driveId": 2
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void createApplicationShouldRejectWhenDuplicate() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setCgpa(9.0);

        PlacementDrive drive = new PlacementDrive();
        drive.setId(2L);
        drive.setMinimumCgpa(7.0);
        drive.setApplicationDeadline("2026-12-31");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(2L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentIdAndDriveId(1L, 2L)).thenReturn(true);

        String requestJson = """
                {
                    "studentId": 1,
                    "driveId": 2
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isConflict());
    }

    @Test
    void createApplicationShouldRejectWhenDeadlinePassed() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setCgpa(9.0);

        PlacementDrive drive = new PlacementDrive();
        drive.setId(2L);
        drive.setMinimumCgpa(7.0);
        drive.setApplicationDeadline("2020-01-01"); // past deadline

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(2L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentIdAndDriveId(1L, 2L)).thenReturn(false);

        String requestJson = """
                {
                    "studentId": 1,
                    "driveId": 2
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatusShouldSucceedForValidStatus() throws Exception {
        Application app = new Application();
        app.setId(1L);
        app.setStatus("APPLIED");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));
        when(applicationRepository.save(any(Application.class))).thenAnswer(i -> i.getArgument(0));

        mockMvc.perform(patch("/api/applications/1/status")
                        .param("status", "SHORTLISTED"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("SHORTLISTED"));
    }

    @Test
    void updateStatusShouldRejectInvalidStatus() throws Exception {
        Application app = new Application();
        app.setId(1L);
        app.setStatus("APPLIED");

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        mockMvc.perform(patch("/api/applications/1/status")
                        .param("status", "BOGUS_STATUS"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void deleteApplicationShouldReturnNoContent() throws Exception {
        Application app = new Application();
        app.setId(1L);

        when(applicationRepository.findById(1L)).thenReturn(Optional.of(app));

        mockMvc.perform(delete("/api/applications/1"))
                .andExpect(status().isNoContent());

        verify(applicationRepository, times(1)).delete(app);
    }

    @Test
    void deleteApplicationShouldReturnNotFoundWhenMissing() throws Exception {
        when(applicationRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(delete("/api/applications/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getApplicationsByStudentShouldReturnListWhenStudentExists() throws Exception {
        when(studentRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByStudentId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/applications/student/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getApplicationsByStudentShouldReturnNotFoundWhenStudentMissing() throws Exception {
        when(studentRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(get("/api/applications/student/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void getApplicationsByDriveShouldReturnListWhenDriveExists() throws Exception {
        when(placementDriveRepository.existsById(1L)).thenReturn(true);
        when(applicationRepository.findByDriveId(1L)).thenReturn(List.of());

        mockMvc.perform(get("/api/applications/drive/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getApplicationsByDriveShouldReturnNotFoundWhenDriveMissing() throws Exception {
        when(placementDriveRepository.existsById(999L)).thenReturn(false);

        mockMvc.perform(get("/api/applications/drive/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createApplicationShouldReturn500WhenDeadlineIsInvalidFormat() throws Exception {
        Student student = new Student();
        student.setId(1L);
        student.setCgpa(8.5);

        PlacementDrive drive = new PlacementDrive();
        drive.setId(2L);
        drive.setMinimumCgpa(7.0);
        drive.setApplicationDeadline("INVALID-DATE");

        when(studentRepository.findById(1L)).thenReturn(Optional.of(student));
        when(placementDriveRepository.findById(2L)).thenReturn(Optional.of(drive));
        when(applicationRepository.existsByStudentIdAndDriveId(1L, 2L)).thenReturn(false);

        String requestJson = """
                {
                    "studentId": 1,
                    "driveId": 2
                }
                """;

        mockMvc.perform(post("/api/applications")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestJson))
                .andExpect(status().isInternalServerError());
    }
}

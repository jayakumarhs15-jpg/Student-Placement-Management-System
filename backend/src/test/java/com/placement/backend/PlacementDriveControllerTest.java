package com.placement.backend;

import com.placement.backend.controller.PlacementDriveController;
import com.placement.backend.entity.PlacementDrive;
import com.placement.backend.repository.PlacementDriveRepository;
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

@WebMvcTest(PlacementDriveController.class)
class PlacementDriveControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PlacementDriveRepository placementDriveRepository;

    @Test
    void getAllDrivesShouldReturnOk() throws Exception {
        when(placementDriveRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/drives"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getDriveByIdShouldReturnDriveWhenFound() throws Exception {
        PlacementDrive drive = new PlacementDrive();
        drive.setId(1L);
        drive.setJobTitle("Software Engineer");
        drive.setCompanyName("Google");
        drive.setLocation("Hyderabad");
        drive.setMinimumCgpa(8.0);
        drive.setDescription("Engineering role");
        drive.setApplicationDeadline("2026-12-31");

        when(placementDriveRepository.findById(1L)).thenReturn(Optional.of(drive));

        mockMvc.perform(get("/api/drives/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Software Engineer"))
                .andExpect(jsonPath("$.companyName").value("Google"))
                .andExpect(jsonPath("$.minimumCgpa").value(8.0));
    }

    @Test
    void getDriveByIdShouldReturn404WhenNotFound() throws Exception {
        when(placementDriveRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/drives/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createDriveShouldSaveAndReturnCreated() throws Exception {
        PlacementDrive saved = new PlacementDrive();
        saved.setId(5L);
        saved.setJobTitle("Data Analyst");
        saved.setCompanyName("Amazon");
        saved.setLocation("Chennai");
        saved.setMinimumCgpa(7.5);
        saved.setDescription("Analytics role");
        saved.setApplicationDeadline("2026-11-30");

        when(placementDriveRepository.save(any(PlacementDrive.class))).thenReturn(saved);

        String json = """
                {
                    "jobTitle": "Data Analyst",
                    "companyName": "Amazon",
                    "location": "Chennai",
                    "minimumCgpa": 7.5,
                    "description": "Analytics role",
                    "applicationDeadline": "2026-11-30"
                }
                """;

        mockMvc.perform(post("/api/drives")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.jobTitle").value("Data Analyst"));
    }

    @Test
    void updateDriveShouldUpdateAndReturn() throws Exception {
        PlacementDrive existing = new PlacementDrive();
        existing.setId(1L);
        existing.setJobTitle("Associate");
        existing.setCompanyName("Infosys");
        existing.setLocation("Mysore");
        existing.setMinimumCgpa(6.5);
        existing.setDescription("Initial desc");
        existing.setApplicationDeadline("2026-10-31");

        when(placementDriveRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(placementDriveRepository.save(any(PlacementDrive.class))).thenAnswer(i -> i.getArgument(0));

        String updateJson = """
                {
                    "jobTitle": "Senior Associate",
                    "companyName": "Infosys",
                    "location": "Bengaluru",
                    "minimumCgpa": 7.0,
                    "description": "Updated desc",
                    "applicationDeadline": "2026-12-15"
                }
                """;

        mockMvc.perform(put("/api/drives/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.jobTitle").value("Senior Associate"))
                .andExpect(jsonPath("$.location").value("Bengaluru"))
                .andExpect(jsonPath("$.minimumCgpa").value(7.0));
    }

    @Test
    void deleteDriveShouldReturnNoContent() throws Exception {
        PlacementDrive existing = new PlacementDrive();
        existing.setId(1L);

        when(placementDriveRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/drives/1"))
                .andExpect(status().isNoContent());

        verify(placementDriveRepository, times(1)).delete(existing);
    }
}

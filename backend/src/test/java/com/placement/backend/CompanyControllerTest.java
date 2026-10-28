package com.placement.backend;

import com.placement.backend.controller.CompanyController;
import com.placement.backend.entity.Company;
import com.placement.backend.repository.CompanyRepository;
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

@WebMvcTest(CompanyController.class)
class CompanyControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CompanyRepository companyRepository;

    @Test
    void getAllCompaniesShouldReturnOk() throws Exception {
        when(companyRepository.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/api/companies"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void getCompanyByIdShouldReturnCompanyWhenFound() throws Exception {
        Company company = new Company();
        company.setId(1L);
        company.setName("Acme Tech");
        company.setIndustry("Software");
        company.setLocation("Bengaluru");
        company.setContactEmail("careers@acme.com");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(company));

        mockMvc.perform(get("/api/companies/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Acme Tech"))
                .andExpect(jsonPath("$.industry").value("Software"));
    }

    @Test
    void getCompanyByIdShouldReturn404WhenNotFound() throws Exception {
        when(companyRepository.findById(999L)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/companies/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void createCompanyShouldSaveAndReturnCreated() throws Exception {
        Company saved = new Company();
        saved.setId(10L);
        saved.setName("NewCo");
        saved.setIndustry("Fintech");
        saved.setLocation("Mumbai");
        saved.setContactEmail("hr@newco.com");

        when(companyRepository.save(any(Company.class))).thenReturn(saved);

        String json = """
                {
                    "name": "NewCo",
                    "industry": "Fintech",
                    "location": "Mumbai",
                    "contactEmail": "hr@newco.com",
                    "description": "Leading Fintech"
                }
                """;

        mockMvc.perform(post("/api/companies")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.name").value("NewCo"));
    }

    @Test
    void updateCompanyShouldUpdateFields() throws Exception {
        Company existing = new Company();
        existing.setId(1L);
        existing.setName("OldName");
        existing.setIndustry("Cloud");
        existing.setLocation("Delhi");
        existing.setContactEmail("old@cloud.com");

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(companyRepository.save(any(Company.class))).thenAnswer(i -> i.getArgument(0));

        String updateJson = """
                {
                    "name": "UpdatedName",
                    "industry": "AI & Cloud",
                    "location": "Noida",
                    "contactEmail": "contact@cloud.com",
                    "description": "AI leader"
                }
                """;

        mockMvc.perform(put("/api/companies/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(updateJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("UpdatedName"))
                .andExpect(jsonPath("$.location").value("Noida"));
    }

    @Test
    void deleteCompanyShouldReturnNoContent() throws Exception {
        Company existing = new Company();
        existing.setId(1L);

        when(companyRepository.findById(1L)).thenReturn(Optional.of(existing));

        mockMvc.perform(delete("/api/companies/1"))
                .andExpect(status().isNoContent());

        verify(companyRepository, times(1)).delete(existing);
    }
}

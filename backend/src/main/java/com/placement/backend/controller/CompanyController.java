package com.placement.backend.controller;

import com.placement.backend.entity.Company;
import com.placement.backend.repository.CompanyRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/companies")
public class CompanyController {

    private final CompanyRepository companyRepository;

    // Constructor: injects the company repository
    public CompanyController(CompanyRepository companyRepository) {
        this.companyRepository = companyRepository;
    }

    // 1. Get all companies
    @GetMapping
    public List<Company> getAllCompanies() {
        return companyRepository.findAll();
    }

    // 2. Register a new company
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Company createCompany(@Valid @RequestBody Company company) {
        company.setId(null);
        return companyRepository.save(company);
    }

    // 3. Get a company by ID
    @GetMapping("/{id}")
    public Company getCompanyById(@PathVariable Long id) {
        return companyRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Company not found"
                        ));
    }

    // 4. Update an existing company
    @PutMapping("/{id}")
    public Company updateCompany(
            @PathVariable Long id,
            @Valid @RequestBody Company updatedCompany) {

        Company existingCompany = companyRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Company not found"
                        ));

        existingCompany.setName(updatedCompany.getName());
        existingCompany.setIndustry(updatedCompany.getIndustry());
        existingCompany.setLocation(updatedCompany.getLocation());
        existingCompany.setContactEmail(updatedCompany.getContactEmail());
        existingCompany.setDescription(updatedCompany.getDescription());

        return companyRepository.save(existingCompany);
    }
    // 5. Delete a company by ID
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteCompany(@PathVariable Long id) {

        Company company = companyRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Company not found"
                        ));

        companyRepository.delete(company);
    }
}

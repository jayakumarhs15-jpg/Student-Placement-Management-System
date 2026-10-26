package com.placement.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "placement_drives")
public class PlacementDrive {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Job title is required")
    @Size(max = 150)
    private String jobTitle;

    @NotBlank(message = "Company name is required")
    @Size(max = 150)
    private String companyName;

    @NotBlank(message = "Job location is required")
    @Size(max = 150)
    private String location;

    @NotNull(message = "Minimum CGPA is required")
    @DecimalMin(value = "0.0", message = "Minimum CGPA cannot be negative")
    private Double minimumCgpa;

    @NotBlank(message = "Job description is required")
    @Size(max = 3000)
    private String description;

    @NotBlank(message = "Application deadline is required")
    private String applicationDeadline;

    public PlacementDrive() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getJobTitle() {
        return jobTitle;
    }

    public void setJobTitle(String jobTitle) {
        this.jobTitle = jobTitle;
    }

    public String getCompanyName() {
        return companyName;
    }

    public void setCompanyName(String companyName) {
        this.companyName = companyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Double getMinimumCgpa() {
        return minimumCgpa;
    }

    public void setMinimumCgpa(Double minimumCgpa) {
        this.minimumCgpa = minimumCgpa;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getApplicationDeadline() {
        return applicationDeadline;
    }

    public void setApplicationDeadline(String applicationDeadline) {
        this.applicationDeadline = applicationDeadline;
    }
}
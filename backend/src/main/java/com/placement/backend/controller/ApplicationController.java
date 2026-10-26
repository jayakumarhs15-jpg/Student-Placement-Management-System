package com.placement.backend.controller;

import com.placement.backend.entity.Application;
import com.placement.backend.entity.Student;
import com.placement.backend.entity.PlacementDrive;
import com.placement.backend.repository.ApplicationRepository;
import com.placement.backend.repository.StudentRepository;
import com.placement.backend.repository.PlacementDriveRepository;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/applications")
public class ApplicationController {

    private final ApplicationRepository applicationRepository;
    private final StudentRepository studentRepository;
    private final PlacementDriveRepository placementDriveRepository;

    public ApplicationController(
            ApplicationRepository applicationRepository,
            StudentRepository studentRepository,
            PlacementDriveRepository placementDriveRepository) {

        this.applicationRepository = applicationRepository;
        this.studentRepository = studentRepository;
        this.placementDriveRepository = placementDriveRepository;
    }

    // 1. Get all applications
    @GetMapping
    public List<Application> getAllApplications() {
        return applicationRepository.findAll();
    }

    // 2. Submit an application
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Application createApplication(
            @Valid @RequestBody Application application) {

        // Confirm that the student exists
        Student student = studentRepository.findById(application.getStudentId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Student not found"));

        // Confirm that the placement drive exists
        PlacementDrive drive = placementDriveRepository
                .findById(application.getDriveId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Placement drive not found"));

        // Check the student's minimum CGPA eligibility
        if (student.getCgpa() < drive.getMinimumCgpa()) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Student does not meet the minimum CGPA requirement");
        }

        // Prevent duplicate applications
        if (applicationRepository.existsByStudentIdAndDriveId(
                application.getStudentId(), application.getDriveId())) {

            throw new ResponseStatusException(
                    HttpStatus.CONFLICT,
                    "Student has already applied for this placement drive");
        }

        // Check the application deadline
        LocalDate deadline;

        try {
            deadline = LocalDate.parse(drive.getApplicationDeadline());
        } catch (Exception exception) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Placement drive has an invalid application deadline");
        }

        if (deadline.isBefore(LocalDate.now())) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Application deadline has passed");
        }

        // Generate application details on the server
        application.setId(null);
        application.setStatus("APPLIED");
        application.setAppliedAt(LocalDateTime.now());

        return applicationRepository.save(application);
    }

    // 3. Get an application by ID
    @GetMapping("/{id}")
    public Application getApplicationById(@PathVariable Long id) {
        return applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));
    }

    // 4. Get applications submitted by a particular student
    @GetMapping("/student/{studentId}")
    public List<Application> getApplicationsByStudent(
            @PathVariable Long studentId) {

        if (!studentRepository.existsById(studentId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Student not found");
        }

        return applicationRepository.findByStudentId(studentId);
    }

    // 5. Get applications for a particular placement drive
    @GetMapping("/drive/{driveId}")
    public List<Application> getApplicationsByDrive(
            @PathVariable Long driveId) {

        if (!placementDriveRepository.existsById(driveId)) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "Placement drive not found");
        }

        return applicationRepository.findByDriveId(driveId);
    }

    // 6. Update an application's status
    @PatchMapping("/{id}/status")
    public Application updateApplicationStatus(
            @PathVariable Long id,
            @RequestParam String status) {

        Application application = applicationRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Application not found"));

        List<String> allowedStatuses = List.of(
                "APPLIED", "SHORTLISTED", "INTERVIEW",
                "SELECTED", "REJECTED");

        String normalizedStatus = status.toUpperCase();

        if (!allowedStatuses.contains(normalizedStatus)) {
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST,
                    "Invalid status. Allowed: " + allowedStatuses);
        }

        application.setStatus(normalizedStatus);

        return applicationRepository.save(application);
    }
}
package com.placement.backend.controller;

import com.placement.backend.entity.PlacementDrive;
import com.placement.backend.repository.PlacementDriveRepository;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drives")
public class PlacementDriveController {

    private final PlacementDriveRepository placementDriveRepository;

    // Constructor: injects the repository
    public PlacementDriveController(
            PlacementDriveRepository placementDriveRepository) {
        this.placementDriveRepository = placementDriveRepository;
    }

    // 1. Get all placement drives
    @GetMapping
    public List<PlacementDrive> getAllDrives() {
        return placementDriveRepository.findAll();
    }

    // 2. Create a placement drive
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlacementDrive createDrive(
            @Valid @RequestBody PlacementDrive drive) {
        drive.setId(null);
        return placementDriveRepository.save(drive);
    }

    // 3. Get a placement drive by ID
    @GetMapping("/{id}")
    public PlacementDrive getDriveById(@PathVariable Long id) {
        return placementDriveRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Placement drive not found"
                        ));
    }

    // 4. Update a placement drive
    @PutMapping("/{id}")
    public PlacementDrive updateDrive(
            @PathVariable Long id,
            @Valid @RequestBody PlacementDrive updatedDrive) {

        PlacementDrive existingDrive = placementDriveRepository
                .findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Placement drive not found"
                        ));

        existingDrive.setJobTitle(updatedDrive.getJobTitle());
        existingDrive.setCompanyName(updatedDrive.getCompanyName());
        existingDrive.setLocation(updatedDrive.getLocation());
        existingDrive.setMinimumCgpa(updatedDrive.getMinimumCgpa());
        existingDrive.setDescription(updatedDrive.getDescription());
        existingDrive.setApplicationDeadline(
                updatedDrive.getApplicationDeadline());

        return placementDriveRepository.save(existingDrive);
    }

    // 5. Delete a placement drive
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDrive(@PathVariable Long id) {

        PlacementDrive drive = placementDriveRepository.findById(id)
                .orElseThrow(() ->
                        new org.springframework.web.server.ResponseStatusException(
                                HttpStatus.NOT_FOUND,
                                "Placement drive not found"
                        ));

        placementDriveRepository.delete(drive);
    }
}
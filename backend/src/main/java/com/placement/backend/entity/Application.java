package com.placement.backend.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;

@Entity
@Table(
    name = "student_applications",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "uk_student_drive",
            columnNames = {"student_id", "drive_id"}
        )
    }
)
public class Application {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Student ID is required")
    @Column(name = "student_id", nullable = false)
    private Long studentId;

    @NotNull(message = "Placement drive ID is required")
    @Column(name = "drive_id", nullable = false)
    private Long driveId;

    @NotBlank(message = "Application status is required")
    @Size(max = 30)
    private String status = "APPLIED";

    private LocalDateTime appliedAt;

    public Application() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getStudentId() {
        return studentId;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getDriveId() {
        return driveId;
    }

    public void setDriveId(Long driveId) {
        this.driveId = driveId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(LocalDateTime appliedAt) {
        this.appliedAt = appliedAt;
    }
}
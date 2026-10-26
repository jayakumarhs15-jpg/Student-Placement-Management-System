package com.placement.backend.repository;

import com.placement.backend.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ApplicationRepository
        extends JpaRepository<Application, Long> {

    // Find all applications submitted by a student
    List<Application> findByStudentId(Long studentId);

    // Find all applications for a placement drive
    List<Application> findByDriveId(Long driveId);

    // Prevent duplicate applications by the same student
    boolean existsByStudentIdAndDriveId(Long studentId, Long driveId);
}
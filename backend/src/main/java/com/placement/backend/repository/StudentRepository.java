package com.placement.backend.repository;

import com.placement.backend.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByEmailIgnoreCase(String email);
}
package com.example.part1.repo;

import com.example.part1.domain.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface PatientRepo extends JpaRepository<Patient, Long> {
    // Basic CRUD operations are inherited from JpaRepository

    // Custom query method to find patient by email
    Patient findByEmail(String email);

    // Check if a patient with given email exists
    boolean existsByEmail(String email);

    // Custom delete method that handles cascading properly
    @Modifying
    @Transactional
    @Query("DELETE FROM Patient p WHERE p.id = :id")
    int deletePatientById(@Param("id") Long id);
}
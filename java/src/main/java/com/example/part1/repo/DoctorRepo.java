package com.example.part1.repo;

import com.example.part1.domain.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DoctorRepo extends JpaRepository<Doctor, Long> {
    // Find doctors by specialisation
    List<Doctor> findBySpecialisation(String specialisation);

    // Find doctor by email
    Doctor findByEmail(String email);

    // Check if a doctor with given email exists
    boolean existsByEmail(String email);
}
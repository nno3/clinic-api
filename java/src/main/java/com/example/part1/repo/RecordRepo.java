package com.example.part1.repo;

import com.example.part1.domain.Appointments;
import com.example.part1.domain.Record;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RecordRepo extends JpaRepository<Record, Long> {
    // Find record by appointment
    Record findByAppointment(Appointments appointment);

    // Find records containing specific diagnosis
    List<Record> findByDiagnosisContaining(String diagnosis);

    // Find records by treatment
    List<Record> findByTreatmentContaining(String treatment);
}
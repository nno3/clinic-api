package com.example.part1.repo;

import com.example.part1.domain.Appointments;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface AppointmentsRepo extends JpaRepository<Appointments, Long> {

    // Retrieve appointments by both patient and doctor
    List<Appointments> findByPatientIdAndDoctorId(Long patientId, Long doctorId);

    // Retrieve appointments by patient, doctor, and status
    List<Appointments> findByPatientIdAndDoctorIdAndStatus(Long patientId, Long doctorId, String status);

    // Retrieve appointments by patient only
    List<Appointments> findByPatientId(Long patientId);

    // Retrieve appointments by doctor only
    List<Appointments> findByDoctorId(Long doctorId);

    // Retrieve appointments by status only
    List<Appointments> findByStatus(String status);

    // Retrieve appointments by patient and status
    List<Appointments> findByPatientIdAndStatus(Long patientId, String status);

    // Retrieve appointments by doctor and status
    List<Appointments> findByDoctorIdAndStatus(Long doctorId, String status);
}

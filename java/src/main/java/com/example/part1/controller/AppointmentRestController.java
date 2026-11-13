package com.example.part1.controller;

import com.example.part1.domain.Appointments;
import com.example.part1.domain.ErrorInfo;
import com.example.part1.repo.AppointmentsRepo;
import com.example.part1.repo.PatientRepo;
import com.example.part1.repo.DoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AppointmentRestController {

    @Autowired
    private AppointmentsRepo appointmentsRepo;

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private DoctorRepo doctorRepo;

    // Get all appointments
    @GetMapping("/appointments")
    public ResponseEntity<?> getAllAppointments(
            @RequestParam(required = false) Long patientId,
            @RequestParam(required = false) Long doctorId,
            @RequestParam(required = false) String status) {

        List<Appointments> appointments;
        if (patientId != null && doctorId != null && status != null) {
            appointments = appointmentsRepo.findByPatientIdAndDoctorIdAndStatus(patientId, doctorId, status);
        } else if (patientId != null && doctorId != null) {
            appointments = appointmentsRepo.findByPatientIdAndDoctorId(patientId, doctorId);
        } else if (patientId != null && status != null) {
            appointments = appointmentsRepo.findByPatientIdAndStatus(patientId, status);
        } else if (doctorId != null && status != null) {
            appointments = appointmentsRepo.findByDoctorIdAndStatus(doctorId, status);
        } else if (patientId != null) {
            appointments = appointmentsRepo.findByPatientId(patientId);
        } else if (doctorId != null) {
            appointments = appointmentsRepo.findByDoctorId(doctorId);
        } else if (status != null) {
            appointments = appointmentsRepo.findByStatus(status);
        } else {
            appointments = appointmentsRepo.findAll();
        }

        if (appointments.isEmpty()) {
            return new ResponseEntity<>(new ErrorInfo("No appointments found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // Create a new appointment
    @PostMapping("/appointments")
    public ResponseEntity<?> createAppointment(@RequestBody Appointments appointment) {
        if (appointment.getAppointmentDate() == null || appointment.getStatus() == null ||
                appointment.getPatient() == null || appointment.getDoctor() == null) {
            return new ResponseEntity<>(
                    new ErrorInfo("Missing required fields: appointmentDate, status, patient, doctor"),
                    HttpStatus.BAD_REQUEST);
        }

        // Verify patient exists
        if (!patientRepo.existsById(appointment.getPatient().getId())) {
            return new ResponseEntity<>(
                    new ErrorInfo("Patient with id " + appointment.getPatient().getId() + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        // Verify doctor exists
        if (!doctorRepo.existsById(appointment.getDoctor().getId())) {
            return new ResponseEntity<>(
                    new ErrorInfo("Doctor with id " + appointment.getDoctor().getId() + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        Appointments savedAppointment = appointmentsRepo.save(appointment);
        return new ResponseEntity<>(savedAppointment, HttpStatus.CREATED);
    }

    // Get a single appointment by ID
    @GetMapping("/appointments/{id}")
    public ResponseEntity<?> getAppointmentById(@PathVariable Long id) {
        Appointments appointment = appointmentsRepo.findById(id).orElse(null);
        if (appointment == null) {
            return new ResponseEntity<>(new ErrorInfo("Appointment with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointment, HttpStatus.OK);
    }

    // Update an appointment
    @PutMapping("/appointments/{id}")
    public ResponseEntity<?> updateAppointment(@PathVariable Long id, @RequestBody Appointments appointmentDetails) {
        Appointments appointment = appointmentsRepo.findById(id).orElse(null);
        if (appointment == null) {
            return new ResponseEntity<>(new ErrorInfo("Appointment with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (appointmentDetails.getAppointmentDate() != null) {
            appointment.setAppointmentDate(appointmentDetails.getAppointmentDate());
        }
        if (appointmentDetails.getStatus() != null) {
            appointment.setStatus(appointmentDetails.getStatus());
        }
        if (appointmentDetails.getNotes() != null) {
            appointment.setNotes(appointmentDetails.getNotes());
        }

        // Update patient if provided and exists
        if (appointmentDetails.getPatient() != null && appointmentDetails.getPatient().getId() != null) {
            if (!patientRepo.existsById(appointmentDetails.getPatient().getId())) {
                return new ResponseEntity<>(
                        new ErrorInfo("Patient with id " + appointmentDetails.getPatient().getId() + " not found"),
                        HttpStatus.NOT_FOUND);
            }
            appointment.setPatient(appointmentDetails.getPatient());
        }

        // Update doctor if provided and exists
        if (appointmentDetails.getDoctor() != null && appointmentDetails.getDoctor().getId() != null) {
            if (!doctorRepo.existsById(appointmentDetails.getDoctor().getId())) {
                return new ResponseEntity<>(
                        new ErrorInfo("Doctor with id " + appointmentDetails.getDoctor().getId() + " not found"),
                        HttpStatus.NOT_FOUND);
            }
            appointment.setDoctor(appointmentDetails.getDoctor());
        }

        Appointments updatedAppointment = appointmentsRepo.save(appointment);
        return new ResponseEntity<>(updatedAppointment, HttpStatus.OK);
    }

    // Delete an appointment
    @DeleteMapping("/appointments/{id}")
    public ResponseEntity<?> deleteAppointment(@PathVariable Long id) {
        if (!appointmentsRepo.existsById(id)) {
            return new ResponseEntity<>(new ErrorInfo("Appointment with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        appointmentsRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // Get medical record for an appointment
    @GetMapping("/appointments/{id}/medical-record")
    public ResponseEntity<?> getAppointmentMedicalRecord(@PathVariable Long id) {
        Appointments appointment = appointmentsRepo.findById(id).orElse(null);
        if (appointment == null) {
            return new ResponseEntity<>(new ErrorInfo("Appointment with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (appointment.getRecord() == null) {
            return new ResponseEntity<>(new ErrorInfo("No medical record exists for this appointment"),
                    HttpStatus.NOT_FOUND);
        }

        return new ResponseEntity<>(appointment.getRecord(), HttpStatus.OK);
    }
}
package com.example.part1.controller;

import com.example.part1.domain.Appointments;
import com.example.part1.domain.ErrorInfo;
import com.example.part1.domain.Patient;
import com.example.part1.repo.AppointmentsRepo;
import com.example.part1.repo.PatientRepo;
import com.example.part1.repo.RecordRepo;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.part1.domain.Record;


import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class PatientRestController {

    @Autowired
    private PatientRepo patientRepo;

    @Autowired
    private AppointmentsRepo appointmentsRepo;

    @Autowired
    private RecordRepo recordRepo;

    // Get all patients
    @GetMapping("/patients")
    public ResponseEntity<?> getAllPatients(
            @RequestParam(required = false) Integer skip,
            @RequestParam(required = false) Integer limit) {
        List<Patient> patients = patientRepo.findAll();

        if (patients.isEmpty()) {
            return new ResponseEntity<>(new ErrorInfo("No patients found"), HttpStatus.NOT_FOUND);
        }

        if (skip != null && limit != null) {
            int fromIndex = Math.min(skip, patients.size());
            int toIndex = Math.min(skip + limit, patients.size());
            patients = patients.subList(fromIndex, toIndex);
        }

        return new ResponseEntity<>(patients, HttpStatus.OK);
    }

    // Create a new patient
    @PostMapping("/patients")
    public ResponseEntity<?> createPatient(@RequestBody Patient patient, UriComponentsBuilder ucBuilder) {
        if (patient.getName() == null || patient.getEmail() == null ||
                patient.getPhoneNumber() == null || patient.getAddress() == null) {
            return new ResponseEntity<>(
                    new ErrorInfo("Missing required fields: name, email, phoneNumber, address"),
                    HttpStatus.BAD_REQUEST);
        }

        Patient savedPatient = patientRepo.save(patient);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/patients/{id}").buildAndExpand(savedPatient.getId()).toUri());
        return new ResponseEntity<>(headers, HttpStatus.CREATED);
    }

    // Get a single patient by ID
    @GetMapping("/patients/{id}")
    public ResponseEntity<?> getPatientById(@PathVariable Long id) {
        Patient patient = patientRepo.findById(id).orElse(null);
        if (patient == null) {
            return new ResponseEntity<>(new ErrorInfo("Patient with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(patient, HttpStatus.OK);
    }

    // Update a patient
    @PutMapping("/patients/{id}")
    public ResponseEntity<?> updatePatient(@PathVariable Long id, @RequestBody Patient patientDetails) {
        Patient patient = patientRepo.findById(id).orElse(null);
        if (patient == null) {
            return new ResponseEntity<>(new ErrorInfo("Patient with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (patientDetails.getName() != null) patient.setName(patientDetails.getName());
        if (patientDetails.getEmail() != null) patient.setEmail(patientDetails.getEmail());
        if (patientDetails.getPhoneNumber() != null) patient.setPhoneNumber(patientDetails.getPhoneNumber());
        if (patientDetails.getAddress() != null) patient.setAddress(patientDetails.getAddress());

        Patient updatedPatient = patientRepo.save(patient);
        return new ResponseEntity<>(updatedPatient, HttpStatus.OK);
    }

    // Delete a patient
    @DeleteMapping("/patients/{id}")
    public ResponseEntity<?> deletePatient(@PathVariable Long id) {
        if (!patientRepo.existsById(id)) {
            return new ResponseEntity<>(new ErrorInfo("Patient with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        try {
            patientRepo.deleteById(id);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (DataIntegrityViolationException e) {
            return new ResponseEntity<>(new ErrorInfo("Cannot delete patient with id " + id + " due to integrity constraints."),
                    HttpStatus.BAD_REQUEST);
        } catch (Exception e) {
            return new ResponseEntity<>(new ErrorInfo("An error occurred while deleting the patient."),
                    HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // Get all appointments for a patient
    @GetMapping("/patients/{id}/appointments")
    public ResponseEntity<?> getPatientAppointments(@PathVariable Long id) {
        if (!patientRepo.existsById(id)) {
            return new ResponseEntity<>(new ErrorInfo("Patient with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Appointments> appointments = appointmentsRepo.findByPatientId(id);
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }

    // Get all medical records for a patient
    @GetMapping("/patients/{id}/medical-records")
    public ResponseEntity<?> getPatientMedicalRecords(@PathVariable Long id) {
        if (!patientRepo.existsById(id)) {
            return new ResponseEntity<>(new ErrorInfo("Patient with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Record> records = new ArrayList<>();
        List<Appointments> appointments = appointmentsRepo.findByPatientId(id);
        for (Appointments appointment : appointments) {
            if (appointment.getRecord() != null) {
                records.add(appointment.getRecord());
            }
        }

        if (records.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(records, HttpStatus.OK);
    }
}
package com.example.part1.controller;

import com.example.part1.domain.Appointments;
import com.example.part1.domain.Doctor;
import com.example.part1.domain.ErrorInfo;
import com.example.part1.repo.AppointmentsRepo;
import com.example.part1.repo.DoctorRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.part1.domain.Record;

import java.util.List;

@RestController
@RequestMapping("/api")
public class DoctorRestController {

    @Autowired
    private DoctorRepo doctorRepo;

    @Autowired
    private AppointmentsRepo appointmentsRepo;

    // Get all doctors
    @GetMapping("/doctors")
    public ResponseEntity<?> getAllDoctors() {
        List<Doctor> doctors = doctorRepo.findAll();
        if (doctors.isEmpty()) {
            return new ResponseEntity<>(new ErrorInfo("No doctors found"), HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctors, HttpStatus.OK);
    }

    // Create a new doctor
    @PostMapping("/doctors")
    public ResponseEntity<?> createDoctor(@RequestBody Doctor doctor, UriComponentsBuilder ucBuilder) {
        if (doctor.getName() == null || doctor.getEmail() == null || doctor.getSpecialisation() == null) {
            return new ResponseEntity<>(
                    new ErrorInfo("Missing required fields: name, email, specialisation"),
                    HttpStatus.BAD_REQUEST);
        }

        Doctor savedDoctor = doctorRepo.save(doctor);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/doctors/{id}").buildAndExpand(savedDoctor.getId()).toUri());
        return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
    }

    // Get a single doctor by ID
    @GetMapping("/doctors/{id}")
    public ResponseEntity<?> getDoctorById(@PathVariable Long id) {
        Doctor doctor = doctorRepo.findById(id).orElse(null);
        if (doctor == null) {
            return new ResponseEntity<>(new ErrorInfo("Doctor with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(doctor, HttpStatus.OK);
    }

    // Update a doctor
    @PutMapping("/doctors/{id}")
    public ResponseEntity<?> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctorDetails) {
        Doctor doctor = doctorRepo.findById(id).orElse(null);
        if (doctor == null) {
            return new ResponseEntity<>(new ErrorInfo("Doctor with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        if (doctorDetails.getName() != null) doctor.setName(doctorDetails.getName());
        if (doctorDetails.getEmail() != null) doctor.setEmail(doctorDetails.getEmail());
        if (doctorDetails.getSpecialisation() != null) doctor.setSpecialisation(doctorDetails.getSpecialisation());
        if (doctorDetails.getPhoneNumber() != null) doctor.setPhoneNumber(doctorDetails.getPhoneNumber());

        Doctor updatedDoctor = doctorRepo.save(doctor);
        return new ResponseEntity<>(updatedDoctor, HttpStatus.OK);
    }

    // Delete a doctor
    @DeleteMapping("/doctors/{id}")
    public ResponseEntity<?> deleteDoctor(@PathVariable Long id) {
        if (!doctorRepo.existsById(id)) {
            return new ResponseEntity<>(new ErrorInfo("Doctor with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }
        doctorRepo.deleteById(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    // Get all appointments for a doctor
    @GetMapping("/doctors/{id}/appointments")
    public ResponseEntity<?> getDoctorAppointments(@PathVariable Long id) {
        if (!doctorRepo.existsById(id)) {
            return new ResponseEntity<>(new ErrorInfo("Doctor with id " + id + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        List<Appointments> appointments = appointmentsRepo.findByDoctorId(id);
        if (appointments.isEmpty()) {
            return new ResponseEntity<>(new ErrorInfo("No appointments found for doctor with id " + id),
                    HttpStatus.NOT_FOUND);
        }
        return new ResponseEntity<>(appointments, HttpStatus.OK);
    }
}
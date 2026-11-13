package com.example.part1.controller;

import com.example.part1.domain.Appointments;
import com.example.part1.domain.ErrorInfo;
import com.example.part1.repo.AppointmentsRepo;
import com.example.part1.repo.RecordRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import com.example.part1.domain.Appointments;
import com.example.part1.domain.Record;

@RestController
@RequestMapping("/api")
public class MedicalRecordRestController {

    @Autowired
    private RecordRepo recordRepo;

    @Autowired
    private AppointmentsRepo appointmentsRepo;

    // Create a medical record
    @PostMapping("/medical-records")
    public ResponseEntity<?> createMedicalRecord(@RequestBody Record record, UriComponentsBuilder ucBuilder) {
        if (record.getAppointment() == null || record.getAppointment().getId() == null ||
                record.getDiagnosis() == null || record.getRecordDate() == null) {
            return new ResponseEntity<>(
                    new ErrorInfo("Missing required fields: appointment, diagnosis, recordDate"),
                    HttpStatus.BAD_REQUEST);
        }

        // Verify appointment exists
        Appointments appointment = appointmentsRepo.findById(record.getAppointment().getId()).orElse(null);
        if (appointment == null) {
            return new ResponseEntity<>(
                    new ErrorInfo("Appointment with id " + record.getAppointment().getId() + " not found"),
                    HttpStatus.NOT_FOUND);
        }

        // Check if appointment already has a record
        if (appointment.getRecord() != null) {
            return new ResponseEntity<>(
                    new ErrorInfo("Appointment already has a medical record"),
                    HttpStatus.BAD_REQUEST);
        }

        record.setAppointment(appointment);
        Record savedRecord = recordRepo.save(record);

        // Update the appointment with the new record
        appointment.setRecord(savedRecord);
        appointmentsRepo.save(appointment);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/api/medical-records/{id}").buildAndExpand(savedRecord.getId()).toUri());
        return new ResponseEntity<>(savedRecord, HttpStatus.CREATED);

    }
}
package com.example.part1.domain;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
public class Appointments {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private Timestamp appointmentDate;
    private String status;
    private String notes;

    /**
     * Many-to-One relationship with Patient.
     * - Many Appointments can belong to one Patient
     * - @JoinColumn creates the patient_id foreign key in Appointment table
     */
    @ManyToOne
    @JsonBackReference
    @JoinColumn(name = "patient_id")
    private Patient patient;

    /**
     * Many-to-One relationship with Doctor.
     * - Many Appointments can belong to one Doctor
     * - @JoinColumn creates the doctor_id foreign key in Appointment table
     */
    @ManyToOne
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;

    /**
     * One-to-One relationship with Record.
     * - One Appointment can have one Record
     * - cascade=CascadeType.ALL: All operations are cascaded to the record
     * - orphanRemoval=true: If appointment is deleted, its record will be deleted
     * - mappedBy="appointment": The Record entity owns the relationship
     */
    @OneToOne(mappedBy = "appointment", cascade = CascadeType.ALL, orphanRemoval = true)
    private Record record;

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Timestamp getAppointmentDate() {
        return appointmentDate;
    }

    public void setAppointmentDate(Timestamp appointmentDate) {
        this.appointmentDate = appointmentDate;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public Patient getPatient() {
        return patient;
    }

    public void setPatient(Patient patient) {
        this.patient = patient;
    }

    public Doctor getDoctor() {
        return doctor;
    }

    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }

    public Record getRecord() {
        return record;
    }

    public void setRecord(Record record) {
        this.record = record;
    }
}
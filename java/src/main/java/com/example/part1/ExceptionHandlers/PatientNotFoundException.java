package com.example.part1.ExceptionHandlers;


public class PatientNotFoundException extends RuntimeException {
    public PatientNotFoundException(Long id) {
        super("Patient not found with ID: " + id);
    }
}

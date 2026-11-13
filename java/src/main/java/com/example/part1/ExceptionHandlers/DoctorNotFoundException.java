package com.example.part1.ExceptionHandlers;

public class DoctorNotFoundException extends RuntimeException {
    public DoctorNotFoundException(Long id) {
        super("Doctor not found with ID: " + id);
    }
}
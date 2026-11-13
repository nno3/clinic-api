package com.example.part1.ExceptionHandlers;

public class AppointmentNotFoundException extends RuntimeException {
    public AppointmentNotFoundException(Long id) {
        super("Appointment not found with ID: " + id);
    }
}

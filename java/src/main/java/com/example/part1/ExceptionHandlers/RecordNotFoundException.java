package com.example.part1.ExceptionHandlers;

public class RecordNotFoundException extends RuntimeException {
    public RecordNotFoundException(Long id) {
        super("Medical record not found with ID: " + id);
    }
}
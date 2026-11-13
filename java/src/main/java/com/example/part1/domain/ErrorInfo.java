package com.example.part1.domain;

public class ErrorInfo {
    private String message;
    private String details;  // additional error details
    private int statusCode;  //  HTTP status code

    public ErrorInfo(String message) {
        this.message = message;
    }

    public ErrorInfo(String message, String details) {
        this.message = message;
        this.details = details;
    }

    public ErrorInfo(String message, String details, int statusCode) {
        this.message = message;
        this.details = details;
        this.statusCode = statusCode;
    }

    // Getters and Setters
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }
}
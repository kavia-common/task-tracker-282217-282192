package com.example.todobackend.exception;

/**
 * PUBLIC_INTERFACE
 * Domain exception for 404 scenarios.
 */
public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}

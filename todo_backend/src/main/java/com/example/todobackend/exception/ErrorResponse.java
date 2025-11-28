package com.example.todobackend.exception;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.List;

/**
 * PUBLIC_INTERFACE
 * Structured error response for API errors.
 */
@Schema(description = "Standard error response")
public class ErrorResponse {

    @Schema(description = "Timestamp of the error")
    private Instant timestamp = Instant.now();

    @Schema(description = "HTTP status code", example = "400")
    private int status;

    @Schema(description = "Error message")
    private String message;

    @Schema(description = "List of field errors when available")
    private List<String> errors;

    public Instant getTimestamp() {
        return timestamp;
    }

    public ErrorResponse setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public int getStatus() {
        return status;
    }

    public ErrorResponse setStatus(int status) {
        this.status = status;
        return this;
    }

    public String getMessage() {
        return message;
    }

    public ErrorResponse setMessage(String message) {
        this.message = message;
        return this;
    }

    public List<String> getErrors() {
        return errors;
    }

    public ErrorResponse setErrors(List<String> errors) {
        this.errors = errors;
        return this;
    }
}

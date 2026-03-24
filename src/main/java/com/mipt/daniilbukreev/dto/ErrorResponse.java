package com.mipt.daniilbukreev.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.time.Instant;
import java.util.Map;

@Schema(description = "Standard error response format")
public class ErrorResponse {
    @Schema(description = "Timestamp of when the error occurred", example = "2026-03-24T10:30:00Z")
    private Instant timestamp;
    @Schema(description = "HTTP status code", example = "400")
    private int status;
    @Schema(description = "Short error message (e.g., 'Bad Request')", example = "Bad Request")
    private String error;
    @Schema(description = "Detailed error message", example = "Validation failed for argument 'title'")
    private String message;
    @Schema(description = "Request URI that caused the error", example = "/api/tasks")
    private String path;
    @Schema(description = "Additional error details, e.g., field validation errors", example = "{ \"title\": \"size must be between 3 and 100\" }")
    private Map<String, Object> details;

    public ErrorResponse(int status, String error, String message, String path) {
        this.timestamp = Instant.now();
        this.status = status;
        this.error = error;
        this.message = message;
        this.path = path;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public Map<String, Object> getDetails() {
        return details;
    }

    public void setDetails(Map<String, Object> details) {
        this.details = details;
    }
}

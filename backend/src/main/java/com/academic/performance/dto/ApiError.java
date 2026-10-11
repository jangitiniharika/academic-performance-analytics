package com.academic.performance.dto;

import java.time.Instant;
import java.util.Map;

/**
 * Standard error response body returned by the API for every failure.
 * Keeping one consistent shape makes the React error handling simple.
 *
 * <p>Example JSON:
 * <pre>
 * {
 *   "timestamp": "2026-01-01T10:15:30Z",
 *   "status": 404,
 *   "error": "Not Found",
 *   "message": "Student not found with id: 42",
 *   "path": "/api/students/42",
 *   "fieldErrors": { "email": "must be a well-formed email address" }
 * }
 * </pre>
 */
public class ApiError {

    private Instant timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> fieldErrors;

    public ApiError() {
        this.timestamp = Instant.now();
    }

    public ApiError(int status, String error, String message, String path) {
        this();
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

    public Map<String, String> getFieldErrors() {
        return fieldErrors;
    }

    public void setFieldErrors(Map<String, String> fieldErrors) {
        this.fieldErrors = fieldErrors;
    }
}

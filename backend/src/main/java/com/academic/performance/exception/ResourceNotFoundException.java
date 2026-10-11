package com.academic.performance.exception;

/**
 * Thrown by the service layer when a requested record does not exist.
 * The {@link GlobalExceptionHandler} converts it into an HTTP 404 response.
 */
public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Convenience constructor: {@code new ResourceNotFoundException("Student", "id", 42)}
     * produces the message {@code "Student not found with id: 42"}.
     */
    public ResourceNotFoundException(String resourceName, String fieldName, Object fieldValue) {
        super(String.format("%s not found with %s: %s", resourceName, fieldName, fieldValue));
    }
}

package com.academic.performance.exception;

/**
 * Thrown when a client tries to create a record that would violate a uniqueness
 * rule (duplicate email, username, roll number, subject code, ...).
 *
 * <p>The {@link GlobalExceptionHandler} converts it into an HTTP 409 Conflict.
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }

    /**
     * Convenience constructor: {@code new DuplicateResourceException("Email", value)}
     * produces the message {@code "Email already exists: value"}.
     */
    public DuplicateResourceException(String fieldName, Object fieldValue) {
        super(String.format("%s already exists: %s", fieldName, fieldValue));
    }
}

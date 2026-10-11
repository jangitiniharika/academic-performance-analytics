package com.academic.performance.dto;

import java.math.BigDecimal;

import com.academic.performance.entity.SubmissionStatus;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for recording/updating an assignment submission (Stage 7).
 *
 * <p>{@code status} is optional on create (defaults to {@code PENDING}) and
 * {@code marks} is filled in when the faculty grades the work.
 */
public record SubmissionRequest(
        @NotNull(message = "Assignment id is required.") Long assignmentId,
        @NotNull(message = "Student id is required.") Long studentId,
        SubmissionStatus status,
        @DecimalMin(value = "0.00", message = "Marks cannot be negative.") BigDecimal marks,
        @Size(max = 500, message = "Feedback must be at most 500 characters.") String feedback) {
}

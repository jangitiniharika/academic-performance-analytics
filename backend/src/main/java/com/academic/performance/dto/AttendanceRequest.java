package com.academic.performance.dto;

import java.time.LocalDate;

import com.academic.performance.entity.AttendanceStatus;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/**
 * Request body for recording/updating one attendance row (Stage 5).
 *
 * <p>The unique rule "one student, one subject, one date" is enforced in the
 * service so a clear {@code 409} is returned for a duplicate.
 */
public record AttendanceRequest(
        @NotNull(message = "Student id is required.") Long studentId,
        @NotNull(message = "Subject id is required.") Long subjectId,
        @NotNull(message = "Class date is required.")
        @PastOrPresent(message = "Class date cannot be in the future.") LocalDate classDate,
        @NotNull(message = "Attendance status is required.") AttendanceStatus status,
        @Size(max = 255, message = "Remarks must be at most 255 characters.") String remarks) {
}

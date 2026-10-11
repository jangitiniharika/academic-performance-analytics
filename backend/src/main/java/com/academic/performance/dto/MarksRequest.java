package com.academic.performance.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

/** Request body for recording a student's marks in an exam (Stage 6). */
public record MarksRequest(
        @NotNull(message = "Student id is required.") Long studentId,
        @NotNull(message = "Exam id is required.") Long examId,
        @NotNull(message = "Marks obtained is required.")
        @DecimalMin(value = "0.00", message = "Marks obtained cannot be negative.") BigDecimal marksObtained) {
}

package com.academic.performance.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request body for creating/updating an {@link com.academic.performance.entity.AcademicResult} (Stage 8). */
public record ResultRequest(
        @NotNull(message = "Student id is required.") Long studentId,
        @NotNull(message = "Subject id is required.") Long subjectId,
        @NotNull(message = "Semester is required.")
        @Min(value = 1, message = "Semester must be between 1 and 12.")
        @Max(value = 12, message = "Semester must be between 1 and 12.") Integer semester,
        @NotBlank(message = "Grade is required.")
        @Size(max = 3, message = "Grade must be at most 3 characters.") String grade,
        @NotNull(message = "Grade points are required.")
        @DecimalMin(value = "0.00", message = "Grade points must be between 0 and 10.")
        @DecimalMax(value = "10.00", message = "Grade points must be between 0 and 10.") BigDecimal gradePoints,
        @NotNull(message = "Credits are required.")
        @Min(value = 0, message = "Credits must be between 0 and 20.")
        @Max(value = 20, message = "Credits must be between 0 and 20.") Integer credits) {
}

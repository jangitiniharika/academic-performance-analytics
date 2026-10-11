package com.academic.performance.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request body for creating/updating an {@link com.academic.performance.entity.Exam} (Stage 6). */
public record ExamRequest(
        @NotBlank(message = "Exam name is required.")
        @Size(max = 100, message = "Exam name must be at most 100 characters.") String name,
        @NotNull(message = "Subject id is required.") Long subjectId,
        @NotNull(message = "Exam date is required.") LocalDate examDate,
        @NotNull(message = "Maximum marks are required.")
        @Min(value = 1, message = "Maximum marks must be at least 1.")
        @Max(value = 1000, message = "Maximum marks must be at most 1000.") Integer maxMarks) {
}

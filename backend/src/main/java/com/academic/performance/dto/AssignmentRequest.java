package com.academic.performance.dto;

import java.time.LocalDate;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** Request body for creating/updating an {@link com.academic.performance.entity.Assignment} (Stage 7). */
public record AssignmentRequest(
        @NotBlank(message = "Title is required.")
        @Size(max = 150, message = "Title must be at most 150 characters.") String title,
        @Size(max = 1000, message = "Description must be at most 1000 characters.") String description,
        @NotNull(message = "Subject id is required.") Long subjectId,
        Long facultyId,
        @NotNull(message = "Due date is required.") LocalDate dueDate,
        @NotNull(message = "Maximum marks are required.")
        @Min(value = 1, message = "Maximum marks must be at least 1.")
        @Max(value = 1000, message = "Maximum marks must be at most 1000.") Integer maxMarks) {
}

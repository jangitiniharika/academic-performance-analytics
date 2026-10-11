package com.academic.performance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.academic.performance.entity.Assignment;

/** Response body describing an {@link Assignment}. */
public record AssignmentResponse(
        Long id,
        String title,
        String description,
        SubjectSummary subject,
        Long facultyId,
        LocalDate dueDate,
        Integer maxMarks,
        LocalDateTime createdAt) {

    public static AssignmentResponse of(Assignment assignment) {
        if (assignment == null) {
            return null;
        }
        return new AssignmentResponse(
                assignment.getId(),
                assignment.getTitle(),
                assignment.getDescription(),
                SubjectSummary.of(assignment.getSubject()),
                assignment.getFaculty() == null ? null : assignment.getFaculty().getId(),
                assignment.getDueDate(),
                assignment.getMaxMarks(),
                assignment.getCreatedAt());
    }
}

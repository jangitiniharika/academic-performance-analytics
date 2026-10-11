package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.academic.performance.entity.AcademicResult;

/** Response body describing an {@link AcademicResult}. */
public record ResultResponse(
        Long id,
        StudentSummary student,
        SubjectSummary subject,
        Integer semester,
        String grade,
        BigDecimal gradePoints,
        Integer credits,
        LocalDateTime createdAt) {

    public static ResultResponse of(AcademicResult result) {
        if (result == null) {
            return null;
        }
        return new ResultResponse(
                result.getId(),
                StudentSummary.of(result.getStudent()),
                SubjectSummary.of(result.getSubject()),
                result.getSemester(),
                result.getGrade(),
                result.getGradePoints(),
                result.getCredits(),
                result.getCreatedAt());
    }
}

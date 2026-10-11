package com.academic.performance.dto;

import java.math.BigDecimal;

/**
 * CGPA summary for a student (Stage 8).
 *
 * <p>{@code cgpa} is {@code null} when the student has no results yet.
 * CGPA = sum(gradePoints x credits) / sum(credits).
 */
public record CgpaResponse(
        Long studentId,
        String rollNumber,
        BigDecimal cgpa,
        int totalCredits,
        BigDecimal totalGradePoints,
        int subjectsCount) {
}

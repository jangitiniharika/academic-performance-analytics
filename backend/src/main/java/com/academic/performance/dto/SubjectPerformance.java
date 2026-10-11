package com.academic.performance.dto;

import java.math.BigDecimal;

/** Per-subject slice of a student's performance (Stage 9). */
public record SubjectPerformance(
        Long subjectId,
        String subjectCode,
        String subjectName,
        BigDecimal attendancePercentage,
        BigDecimal marksPercentage,
        long examCount) {
}

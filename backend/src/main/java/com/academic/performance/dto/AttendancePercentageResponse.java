package com.academic.performance.dto;

import java.math.BigDecimal;
import java.util.List;

/**
 * A student's attendance summary (Stage 5).
 *
 * <p>{@code percentage} is {@code null} when the student has no attendance yet
 * (missing data is reported as "unknown", never as 0).
 */
public record AttendancePercentageResponse(
        Long studentId,
        String rollNumber,
        long attended,
        long total,
        BigDecimal percentage,
        List<SubjectAttendancePercentage> bySubject) {
}

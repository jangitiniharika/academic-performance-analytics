package com.academic.performance.dto;

import java.math.BigDecimal;
import java.util.List;

/** Class-level analytics for one subject (Stage 9). */
public record ClassAnalyticsResponse(
        Long subjectId,
        String subjectCode,
        String subjectName,
        long studentCount,
        BigDecimal averageMarksPercentage,
        BigDecimal averageAttendancePercentage,
        List<DistributionBucket> distribution) {
}

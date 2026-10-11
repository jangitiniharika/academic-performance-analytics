package com.academic.performance.dto;

import java.math.BigDecimal;
import java.util.List;

/** Full analytics picture for one student (Stage 9), including a fresh prediction. */
public record StudentAnalyticsResponse(
        Long studentId,
        String rollNumber,
        String fullName,
        String departmentCode,
        BigDecimal attendancePercentage,
        BigDecimal averageMarksPercentage,
        BigDecimal assignmentPercentage,
        BigDecimal cgpa,
        PredictionResponse prediction,
        List<SubjectPerformance> subjects,
        List<TrendPoint> marksTrend) {
}

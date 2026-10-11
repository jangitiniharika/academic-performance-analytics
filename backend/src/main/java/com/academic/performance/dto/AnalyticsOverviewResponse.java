package com.academic.performance.dto;

import java.math.BigDecimal;

/**
 * Institution-wide analytics summary (Stage 9).
 *
 * <p>All averages are {@code null} when there is no underlying data, never 0, so
 * the UI can honestly say "no data yet".
 */
public record AnalyticsOverviewResponse(
        long totalStudents,
        long totalFaculty,
        long totalSubjects,
        long totalDepartments,
        BigDecimal averageAttendancePercentage,
        BigDecimal averageMarksPercentage,
        BigDecimal averageCgpa,
        long highRiskStudents,
        long mediumRiskStudents,
        long openAlerts) {
}

package com.academic.performance.dto;

import java.math.BigDecimal;

/** Attendance percentage for a single subject. */
public record SubjectAttendancePercentage(
        SubjectSummary subject,
        long attended,
        long total,
        BigDecimal percentage) {
}

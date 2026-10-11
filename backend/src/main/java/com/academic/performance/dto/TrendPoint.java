package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDate;

/** One point on a student's marks-trend chart (Stage 9). */
public record TrendPoint(String label, String subjectCode, LocalDate date, BigDecimal percentage) {
}

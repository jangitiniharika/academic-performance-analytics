package com.academic.performance.dto;

import java.math.BigDecimal;

import com.academic.performance.entity.RiskLevel;

/** A student flagged as needing attention, used by the dashboards (Stage 11). */
public record RiskStudent(
        Long studentId,
        String rollNumber,
        String fullName,
        String departmentCode,
        BigDecimal predictedScore,
        RiskLevel riskLevel) {
}

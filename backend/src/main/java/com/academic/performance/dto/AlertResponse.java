package com.academic.performance.dto;

import java.time.LocalDateTime;

import com.academic.performance.entity.AlertSeverity;
import com.academic.performance.entity.AlertType;
import com.academic.performance.entity.PerformanceAlert;

/** Response body describing an early-warning {@link PerformanceAlert} (Stage 11). */
public record AlertResponse(
        Long id,
        StudentSummary student,
        AlertType type,
        AlertSeverity severity,
        String message,
        boolean acknowledged,
        java.time.LocalDateTime createdAt,
        LocalDateTime resolvedAt) {

    public static AlertResponse of(PerformanceAlert alert) {
        if (alert == null) {
            return null;
        }
        return new AlertResponse(
                alert.getId(),
                StudentSummary.of(alert.getStudent()),
                alert.getType(),
                alert.getSeverity(),
                alert.getMessage(),
                alert.isAcknowledged(),
                alert.getCreatedAt(),
                alert.getResolvedAt());
    }
}

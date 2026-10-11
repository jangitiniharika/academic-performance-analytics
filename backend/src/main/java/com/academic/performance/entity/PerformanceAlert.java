package com.academic.performance.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * An early-warning alert raised for a student (low attendance, declining marks,
 * or poor predicted performance). Created by the alert service in Stage 11.
 *
 * <p>{@code acknowledged} lets faculty/admin mark an alert as seen;
 * {@code resolvedAt} records when the underlying issue was fixed.
 */
@Entity
@Table(
        name = "performance_alerts",
        indexes = {
                @Index(name = "idx_alerts_student", columnList = "student_id"),
                @Index(name = "idx_alerts_acknowledged", columnList = "acknowledged")
        })
public class PerformanceAlert extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "type", nullable = false, length = 20)
    private AlertType type;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false, length = 10)
    private AlertSeverity severity;

    @NotBlank
    @Size(max = 500)
    @Column(name = "message", nullable = false, length = 500)
    private String message;

    @Column(name = "acknowledged", nullable = false)
    private boolean acknowledged = false;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    public PerformanceAlert() {
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public AlertType getType() {
        return type;
    }

    public void setType(AlertType type) {
        this.type = type;
    }

    public AlertSeverity getSeverity() {
        return severity;
    }

    public void setSeverity(AlertSeverity severity) {
        this.severity = severity;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isAcknowledged() {
        return acknowledged;
    }

    public void setAcknowledged(boolean acknowledged) {
        this.acknowledged = acknowledged;
    }

    public LocalDateTime getResolvedAt() {
        return resolvedAt;
    }

    public void setResolvedAt(LocalDateTime resolvedAt) {
        this.resolvedAt = resolvedAt;
    }
}

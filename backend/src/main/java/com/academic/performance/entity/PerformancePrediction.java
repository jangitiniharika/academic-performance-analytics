package com.academic.performance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * The stored result of the rule-based prediction for one student.
 *
 * <p>A student can have many predictions over time (one per run); the latest one
 * is fetched with
 * {@code findTopByStudentIdOrderByGeneratedAtDesc(...)}. This table is written by
 * the prediction service in Stage 10.
 *
 * <p>This is a transparent weighted estimate, NOT a trained machine-learning
 * model (see README).
 */
@Entity
@Table(
        name = "performance_predictions",
        indexes = @Index(name = "idx_predictions_student", columnList = "student_id"))
public class PerformancePrediction extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("100.00")
    @Column(name = "predicted_score", nullable = false, precision = 5, scale = 2)
    private BigDecimal predictedScore;

    @NotBlank
    @Size(max = 3)
    @Column(name = "predicted_grade", nullable = false, length = 3)
    private String predictedGrade;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "risk_level", nullable = false, length = 10)
    private RiskLevel riskLevel;

    @Size(max = 1000)
    @Column(name = "recommendation", length = 1000)
    private String recommendation;

    @Column(name = "generated_at", nullable = false)
    private LocalDateTime generatedAt;

    public PerformancePrediction() {
    }

    /** Defaults the generation timestamp if the service did not set one. */
    @PrePersist
    void stampGeneratedAt() {
        if (generatedAt == null) {
            generatedAt = LocalDateTime.now();
        }
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public BigDecimal getPredictedScore() {
        return predictedScore;
    }

    public void setPredictedScore(BigDecimal predictedScore) {
        this.predictedScore = predictedScore;
    }

    public String getPredictedGrade() {
        return predictedGrade;
    }

    public void setPredictedGrade(String predictedGrade) {
        this.predictedGrade = predictedGrade;
    }

    public RiskLevel getRiskLevel() {
        return riskLevel;
    }

    public void setRiskLevel(RiskLevel riskLevel) {
        this.riskLevel = riskLevel;
    }

    public String getRecommendation() {
        return recommendation;
    }

    public void setRecommendation(String recommendation) {
        this.recommendation = recommendation;
    }

    public LocalDateTime getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(LocalDateTime generatedAt) {
        this.generatedAt = generatedAt;
    }
}

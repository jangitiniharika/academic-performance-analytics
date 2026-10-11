package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import com.academic.performance.entity.PerformancePrediction;
import com.academic.performance.entity.RiskLevel;

/**
 * Result of the transparent, rule-based prediction (Stage 10).
 *
 * <p>{@code previousScore}, {@code internalsScore}, {@code attendanceScore} and
 * {@code assignmentsScore} are the normalised (0-100) inputs, each {@code null}
 * when that indicator had no data. {@code missingIndicators} names them, and the
 * final score is a weighted average over whichever indicators were available
 * (weights re-normalised). This is an <strong>estimate</strong>, not an ML model.
 */
public record PredictionResponse(
        Long studentId,
        String rollNumber,
        BigDecimal predictedScore,
        String predictedGrade,
        RiskLevel riskLevel,
        String recommendation,
        BigDecimal previousScore,
        BigDecimal internalsScore,
        BigDecimal attendanceScore,
        BigDecimal assignmentsScore,
        List<String> missingIndicators,
        LocalDateTime generatedAt) {

    /** Rebuilds a response from a stored prediction (component scores unavailable). */
    public static PredictionResponse of(PerformancePrediction prediction) {
        if (prediction == null) {
            return null;
        }
        return new PredictionResponse(
                prediction.getStudent() == null ? null : prediction.getStudent().getId(),
                prediction.getStudent() == null ? null : prediction.getStudent().getRollNumber(),
                prediction.getPredictedScore(),
                prediction.getPredictedGrade(),
                prediction.getRiskLevel(),
                prediction.getRecommendation(),
                null, null, null, null,
                List.of(),
                prediction.getGeneratedAt());
    }
}

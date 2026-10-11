package com.academic.performance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.PredictionResponse;
import com.academic.performance.entity.PerformancePrediction;
import com.academic.performance.entity.RiskLevel;
import com.academic.performance.entity.Student;
import com.academic.performance.repository.PerformancePredictionRepository;

/**
 * The transparent, rule-based prediction engine (Stage 10).
 *
 * <p>Each indicator is normalised to 0-100, then combined with fixed weights:
 * <pre>
 *   score = 0.40 x previous + 0.30 x internals + 0.15 x attendance + 0.15 x assignments
 * </pre>
 *
 * <p>When an indicator has no data its term is dropped and the remaining weights
 * are re-normalised (divided by the total of the available weights). The response
 * lists the missing indicators, so missing data is never silently treated as zero.
 * This is an <strong>estimate</strong>, not a trained machine-learning model.
 */
@Service
public class PredictionService {

    private static final BigDecimal W_PREVIOUS = new BigDecimal("0.40");
    private static final BigDecimal W_INTERNALS = new BigDecimal("0.30");
    private static final BigDecimal W_ATTENDANCE = new BigDecimal("0.15");
    private static final BigDecimal W_ASSIGNMENTS = new BigDecimal("0.15");
    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);

    private final MetricsService metricsService;
    private final PerformancePredictionRepository predictionRepository;

    public PredictionService(MetricsService metricsService,
                             PerformancePredictionRepository predictionRepository) {
        this.metricsService = metricsService;
        this.predictionRepository = predictionRepository;
    }

    /** Pure calculation - does NOT write to the database. */
    @Transactional(readOnly = true)
    public PredictionResponse evaluate(Student student) {
        MetricsService.StudentMetrics metrics = metricsService.forStudent(student);

        BigDecimal previous = metrics.cgpa() == null
                ? null
                : metrics.cgpa().multiply(BigDecimal.TEN).min(HUNDRED);
        BigDecimal internals = metrics.averageMarksPercentage();
        BigDecimal attendance = metrics.attendancePercentage();
        BigDecimal assignments = metrics.assignmentPercentage();

        BigDecimal weightedSum = BigDecimal.ZERO;
        BigDecimal weightTotal = BigDecimal.ZERO;
        List<String> missing = new ArrayList<>();

        if (previous == null) {
            missing.add("Previous results (CGPA)");
        } else {
            weightedSum = weightedSum.add(W_PREVIOUS.multiply(previous));
            weightTotal = weightTotal.add(W_PREVIOUS);
        }
        if (internals == null) {
            missing.add("Internal marks");
        } else {
            weightedSum = weightedSum.add(W_INTERNALS.multiply(internals));
            weightTotal = weightTotal.add(W_INTERNALS);
        }
        if (attendance == null) {
            missing.add("Attendance");
        } else {
            weightedSum = weightedSum.add(W_ATTENDANCE.multiply(attendance));
            weightTotal = weightTotal.add(W_ATTENDANCE);
        }
        if (assignments == null) {
            missing.add("Assignments");
        } else {
            weightedSum = weightedSum.add(W_ASSIGNMENTS.multiply(assignments));
            weightTotal = weightTotal.add(W_ASSIGNMENTS);
        }

        BigDecimal score = weightTotal.signum() == 0
                ? null
                : weightedSum.divide(weightTotal, 2, RoundingMode.HALF_UP);

        String grade = score == null ? "N/A" : gradeFor(score);
        RiskLevel risk = score == null ? RiskLevel.HIGH : riskFor(score);
        String recommendation = recommendation(score, previous, internals, attendance, assignments);

        return new PredictionResponse(
                student.getId(),
                student.getRollNumber(),
                score,
                grade,
                risk,
                recommendation,
                previous,
                internals,
                attendance,
                assignments,
                missing,
                null);
    }

    /**
     * Evaluates the student and, when at least one indicator exists, stores a
     * snapshot so dashboards and alerts can show a stable latest value.
     */
    @Transactional
    public PredictionResponse generate(Long studentId) {
        Student student = metricsService.requireStudent(studentId);
        PredictionResponse evaluation = evaluate(student);

        if (evaluation.predictedScore() == null) {
            return evaluation; // nothing to persist; response names the missing data
        }

        PerformancePrediction prediction = new PerformancePrediction();
        prediction.setStudent(student);
        prediction.setPredictedScore(evaluation.predictedScore());
        prediction.setPredictedGrade(evaluation.predictedGrade());
        prediction.setRiskLevel(evaluation.riskLevel());
        prediction.setRecommendation(evaluation.recommendation());
        PerformancePrediction saved = predictionRepository.save(prediction);

        return new PredictionResponse(
                student.getId(),
                student.getRollNumber(),
                saved.getPredictedScore(),
                saved.getPredictedGrade(),
                saved.getRiskLevel(),
                saved.getRecommendation(),
                evaluation.previousScore(),
                evaluation.internalsScore(),
                evaluation.attendanceScore(),
                evaluation.assignmentsScore(),
                evaluation.missingIndicators(),
                saved.getGeneratedAt());
    }

    /** The most recent stored prediction, if any. */
    @Transactional(readOnly = true)
    public PredictionResponse latest(Long studentId) {
        return predictionRepository.findTopByStudentIdOrderByGeneratedAtDesc(studentId)
                .map(PredictionResponse::of)
                .orElse(null);
    }

    // --------------------------------------------------------------- helpers

    private String gradeFor(BigDecimal score) {
        double value = score.doubleValue();
        if (value >= 85) {
            return "A";
        }
        if (value >= 70) {
            return "B";
        }
        if (value >= 55) {
            return "C";
        }
        if (value >= 40) {
            return "D";
        }
        return "F";
    }

    private RiskLevel riskFor(BigDecimal score) {
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            return RiskLevel.LOW;
        }
        if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            return RiskLevel.MEDIUM;
        }
        return RiskLevel.HIGH;
    }

    private String recommendation(BigDecimal score, BigDecimal previous, BigDecimal internals,
                                  BigDecimal attendance, BigDecimal assignments) {
        if (score == null) {
            return "Not enough academic data to generate a prediction yet. Record attendance, marks, "
                    + "assignments and previous results first.";
        }

        List<Indicator> indicators = new ArrayList<>();
        if (previous != null) {
            indicators.add(new Indicator("previous results", previous,
                    "work on overall academic fundamentals to lift the CGPA"));
        }
        if (internals != null) {
            indicators.add(new Indicator("internal marks", internals,
                    "revise the internal exam topics and take more practice tests"));
        }
        if (attendance != null) {
            indicators.add(new Indicator("attendance", attendance,
                    "attend classes more regularly; attendance carries 15% of the score"));
        }
        if (assignments != null) {
            indicators.add(new Indicator("assignments", assignments,
                    "submit assignments on time and aim for higher scores"));
        }

        Indicator weakest = null;
        for (Indicator indicator : indicators) {
            if (weakest == null || indicator.value().compareTo(weakest.value()) < 0) {
                weakest = indicator;
            }
        }

        String focus = weakest == null
                ? "keep studying consistently"
                : "focus on " + weakest.name() + " (" + weakest.advice() + ")";

        String band;
        if (score.compareTo(BigDecimal.valueOf(70)) >= 0) {
            band = "On track";
        } else if (score.compareTo(BigDecimal.valueOf(50)) >= 0) {
            band = "Needs attention";
        } else {
            band = "At risk";
        }
        return band + ": predicted score " + score + "/100 - " + focus + ".";
    }

    /** A single indicator used to choose the recommendation focus. */
    private record Indicator(String name, BigDecimal value, String advice) {
    }
}

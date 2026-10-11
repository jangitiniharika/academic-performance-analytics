package com.academic.performance.service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.AlertResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.PredictionResponse;
import com.academic.performance.entity.AlertSeverity;
import com.academic.performance.entity.AlertType;
import com.academic.performance.entity.PerformanceAlert;
import com.academic.performance.entity.RiskLevel;
import com.academic.performance.entity.Student;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.PerformanceAlertRepository;

/**
 * Early-warning alerts (Stage 11).
 *
 * <p>Three transparent rules raise an alert (never more than one open alert of a
 * given type per student, so re-running generation is safe):
 * <ul>
 *   <li><b>ATTENDANCE</b> - below 75% (HIGH below 60%).</li>
 *   <li><b>MARKS</b>      - average internal marks below 50% (HIGH below 40%).</li>
 *   <li><b>PREDICTION</b> - the weighted prediction falls in the MEDIUM or HIGH risk band.</li>
 * </ul>
 */
@Service
public class AlertService {

    private static final BigDecimal ATTENDANCE_WARNING = BigDecimal.valueOf(75);
    private static final BigDecimal ATTENDANCE_CRITICAL = BigDecimal.valueOf(60);
    private static final BigDecimal MARKS_WARNING = BigDecimal.valueOf(50);
    private static final BigDecimal MARKS_CRITICAL = BigDecimal.valueOf(40);

    private final PerformanceAlertRepository alertRepository;
    private final MetricsService metricsService;
    private final PredictionService predictionService;

    public AlertService(PerformanceAlertRepository alertRepository,
                        MetricsService metricsService,
                        PredictionService predictionService) {
        this.alertRepository = alertRepository;
        this.metricsService = metricsService;
        this.predictionService = predictionService;
    }

    @Transactional(readOnly = true)
    public PageResponse<AlertResponse> list(Long studentId, AlertSeverity severity,
                                            Boolean acknowledged, Pageable pageable) {
        Page<PerformanceAlert> page = alertRepository.search(studentId, severity, acknowledged, pageable);
        return PageResponse.from(page.map(AlertResponse::of));
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> forStudent(Long studentId) {
        return alertRepository.findByStudentIdOrderByCreatedAtDesc(studentId)
                .stream().map(AlertResponse::of).toList();
    }

    @Transactional(readOnly = true)
    public long openCount() {
        return alertRepository.countByAcknowledgedFalse();
    }

    @Transactional(readOnly = true)
    public List<AlertResponse> recentOpen(int limit) {
        return alertRepository.findByAcknowledgedFalseOrderByCreatedAtDesc().stream()
                .limit(limit)
                .map(AlertResponse::of)
                .toList();
    }

    @Transactional
    public AlertResponse acknowledge(Long id) {
        PerformanceAlert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        if (!alert.isAcknowledged()) {
            alert.setAcknowledged(true);
            alert.setResolvedAt(LocalDateTime.now());
            alertRepository.save(alert);
        }
        return AlertResponse.of(alert);
    }

    /**
     * Re-evaluates every rule for one student, raising any new alerts.
     *
     * @return the alerts created by this run (possibly empty).
     */
    @Transactional
    public List<AlertResponse> generateForStudent(Long studentId) {
        Student student = metricsService.requireStudent(studentId);
        MetricsService.StudentMetrics metrics = metricsService.forStudent(student);
        PredictionResponse prediction = predictionService.generate(studentId);

        List<PerformanceAlert> created = new ArrayList<>();

        BigDecimal attendance = metrics.attendancePercentage();
        if (attendance != null && attendance.compareTo(ATTENDANCE_WARNING) < 0) {
            AlertSeverity severity = attendance.compareTo(ATTENDANCE_CRITICAL) < 0
                    ? AlertSeverity.HIGH : AlertSeverity.MEDIUM;
            addIfAbsent(student, created, AlertType.ATTENDANCE, severity,
                    "Attendance is " + attendance + "%, below the required 75%.");
        }

        BigDecimal marks = metrics.averageMarksPercentage();
        if (marks != null && marks.compareTo(MARKS_WARNING) < 0) {
            AlertSeverity severity = marks.compareTo(MARKS_CRITICAL) < 0
                    ? AlertSeverity.HIGH : AlertSeverity.MEDIUM;
            addIfAbsent(student, created, AlertType.MARKS, severity,
                    "Average internal marks are " + marks + "%, below the 50% pass benchmark.");
        }

        if (prediction.riskLevel() == RiskLevel.HIGH) {
            addIfAbsent(student, created, AlertType.PREDICTION, AlertSeverity.HIGH,
                    "Predicted performance is in the HIGH risk band ("
                            + describeScore(prediction) + ").");
        } else if (prediction.riskLevel() == RiskLevel.MEDIUM) {
            addIfAbsent(student, created, AlertType.PREDICTION, AlertSeverity.MEDIUM,
                    "Predicted performance needs attention (" + prediction.predictedScore() + "/100).");
        }

        return created.stream().map(AlertResponse::of).toList();
    }

    // --------------------------------------------------------------- helpers

    private String describeScore(PredictionResponse prediction) {
        return prediction.predictedScore() == null
                ? "not enough data"
                : prediction.predictedScore() + "/100";
    }

    /**
     * Creates the alert unless an open alert of the same type already exists.
     *
     * @return {@code true} when a new row was written.
     */
    private boolean addIfAbsent(Student student, List<PerformanceAlert> created,
                                AlertType type, AlertSeverity severity, String message) {
        if (alertRepository.existsByStudentIdAndTypeAndAcknowledgedFalse(student.getId(), type)) {
            return false;
        }
        PerformanceAlert alert = new PerformanceAlert();
        alert.setStudent(student);
        alert.setType(type);
        alert.setSeverity(severity);
        alert.setMessage(message);
        alert.setAcknowledged(false);
        created.add(alertRepository.save(alert));
        return true;
    }
}

package com.academic.performance.service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.AdminDashboardResponse;
import com.academic.performance.dto.AlertResponse;
import com.academic.performance.dto.FacultyDashboardResponse;
import com.academic.performance.dto.RiskStudent;
import com.academic.performance.dto.StudentAnalyticsResponse;
import com.academic.performance.dto.StudentDashboardResponse;
import com.academic.performance.entity.RiskLevel;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.SubmissionStatus;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AssignmentRepository;
import com.academic.performance.repository.AssignmentSubmissionRepository;
import com.academic.performance.repository.PerformanceAlertRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;

/**
 * Assembles the role dashboards (Stages 15-17) from the analytics, prediction
 * and alert services, so each screen needs a single round-trip.
 */
@Service
public class DashboardService {

    private static final int AT_RISK_LIMIT = 10;
    private static final int RECENT_ALERT_LIMIT = 10;

    private final AnalyticsService analyticsService;
    private final AlertService alertService;
    private final MetricsService metricsService;
    private final PredictionService predictionService;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;
    private final AssignmentRepository assignmentRepository;
    private final AssignmentSubmissionRepository submissionRepository;
    private final PerformanceAlertRepository alertRepository;

    public DashboardService(AnalyticsService analyticsService,
                            AlertService alertService,
                            MetricsService metricsService,
                            PredictionService predictionService,
                            StudentRepository studentRepository,
                            SubjectRepository subjectRepository,
                            AssignmentRepository assignmentRepository,
                            AssignmentSubmissionRepository submissionRepository,
                            PerformanceAlertRepository alertRepository) {
        this.analyticsService = analyticsService;
        this.alertService = alertService;
        this.metricsService = metricsService;
        this.predictionService = predictionService;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
        this.assignmentRepository = assignmentRepository;
        this.submissionRepository = submissionRepository;
        this.alertRepository = alertRepository;
    }

    @Transactional(readOnly = true)
    public AdminDashboardResponse adminDashboard() {
        return new AdminDashboardResponse(
                analyticsService.overview(),
                atRiskStudents(),
                alertService.recentOpen(RECENT_ALERT_LIMIT));
    }

    /** Faculty dashboard over all data (the ADMIN view). */
    @Transactional(readOnly = true)
    public FacultyDashboardResponse facultyDashboard() {
        return facultyDashboard(null, null);
    }

    /**
     * Faculty dashboard, optionally limited to one department (students and
     * alerts) and one faculty member (subjects, assignments, pending
     * submissions). A {@code null} scope means "the whole institution" and is
     * used for ADMIN callers.
     */
    @Transactional(readOnly = true)
    public FacultyDashboardResponse facultyDashboard(Long departmentId, Long facultyId) {
        long totalStudents = departmentId == null
                ? studentRepository.count()
                : studentRepository.countByDepartmentId(departmentId);

        long totalSubjects = facultyId == null
                ? subjectRepository.count()
                : subjectRepository.countByFacultyId(facultyId);

        long totalAssignments = facultyId == null
                ? assignmentRepository.count()
                : assignmentRepository.countByFacultyId(facultyId);

        long pendingSubmissions = facultyId == null
                ? submissionRepository.countByStatus(SubmissionStatus.PENDING)
                : submissionRepository.countByStatusAndAssignmentFacultyId(
                        SubmissionStatus.PENDING, facultyId);

        long openAlerts = departmentId == null
                ? alertRepository.countByAcknowledgedFalse()
                : alertRepository.countByAcknowledgedFalseAndStudentDepartmentId(departmentId);

        return new FacultyDashboardResponse(
                totalStudents,
                totalSubjects,
                totalAssignments,
                pendingSubmissions,
                openAlerts,
                atRiskStudents(departmentId),
                alertService.recentOpen(departmentId, RECENT_ALERT_LIMIT));
    }

    @Transactional(readOnly = true)
    public StudentDashboardResponse studentDashboard(String username) {
        Student student = studentRepository.findByUser_Username(username)
                .orElseThrow(() -> new ResourceNotFoundException("Student profile", "username", username));

        StudentAnalyticsResponse analytics = analyticsService.studentAnalytics(student.getId());
        List<AlertResponse> alerts = alertRepository
                .findByStudentIdOrderByCreatedAtDesc(student.getId())
                .stream()
                .map(AlertResponse::of)
                .toList();

        return new StudentDashboardResponse(analytics, alerts);
    }

    /** Students whose fresh prediction lands in the MEDIUM or HIGH band. */
    @Transactional(readOnly = true)
    public List<RiskStudent> atRiskStudents() {
        return atRiskStudents(null);
    }

    /**
     * Students whose fresh prediction lands in the MEDIUM or HIGH band, limited
     * to one department when {@code departmentId} is not {@code null}.
     */
    @Transactional(readOnly = true)
    public List<RiskStudent> atRiskStudents(Long departmentId) {
        List<Student> students = departmentId == null
                ? studentRepository.findAll()
                : studentRepository.findByDepartmentId(departmentId);

        List<RiskStudent> atRisk = new ArrayList<>();
        for (Student student : students) {
            var prediction = predictionService.evaluate(student);
            if (prediction.riskLevel() == RiskLevel.LOW) {
                continue;
            }
            atRisk.add(new RiskStudent(
                    student.getId(),
                    student.getRollNumber(),
                    student.getFullName(),
                    student.getDepartment() == null ? null : student.getDepartment().getCode(),
                    prediction.predictedScore(),
                    prediction.riskLevel()));
        }
        atRisk.sort(Comparator.comparing(RiskStudent::predictedScore,
                Comparator.nullsLast(Comparator.naturalOrder())));
        return atRisk.size() > AT_RISK_LIMIT ? atRisk.subList(0, AT_RISK_LIMIT) : atRisk;
    }
}

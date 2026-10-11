package com.academic.performance.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.AlertResponse;
import com.academic.performance.dto.AttendancePercentageResponse;
import com.academic.performance.dto.AttendanceResponse;
import com.academic.performance.dto.CgpaResponse;
import com.academic.performance.dto.MarksResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.PredictionResponse;
import com.academic.performance.dto.ResultResponse;
import com.academic.performance.dto.StudentAnalyticsResponse;
import com.academic.performance.dto.StudentDashboardResponse;
import com.academic.performance.dto.SubmissionResponse;
import com.academic.performance.entity.Student;
import com.academic.performance.service.AlertService;
import com.academic.performance.service.AnalyticsService;
import com.academic.performance.service.AttendanceService;
import com.academic.performance.service.DashboardService;
import com.academic.performance.service.MarksService;
import com.academic.performance.service.PredictionService;
import com.academic.performance.service.ResultService;
import com.academic.performance.service.StudentService;
import com.academic.performance.service.SubmissionService;

/**
 * The logged-in student's own academic data.
 *
 * <p>Every method resolves the caller from the JWT (never from a request
 * parameter), so a student can only ever see their own attendance, marks,
 * assignments, results, prediction and alerts. The {@code /api/student/**}
 * namespace is STUDENT-only in {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/api/student")
public class StudentDataController {

    private final StudentService studentService;
    private final AttendanceService attendanceService;
    private final MarksService marksService;
    private final SubmissionService submissionService;
    private final ResultService resultService;
    private final PredictionService predictionService;
    private final AlertService alertService;
    private final AnalyticsService analyticsService;
    private final DashboardService dashboardService;

    public StudentDataController(StudentService studentService,
                                 AttendanceService attendanceService,
                                 MarksService marksService,
                                 SubmissionService submissionService,
                                 ResultService resultService,
                                 PredictionService predictionService,
                                 AlertService alertService,
                                 AnalyticsService analyticsService,
                                 DashboardService dashboardService) {
        this.studentService = studentService;
        this.attendanceService = attendanceService;
        this.marksService = marksService;
        this.submissionService = submissionService;
        this.resultService = resultService;
        this.predictionService = predictionService;
        this.alertService = alertService;
        this.analyticsService = analyticsService;
        this.dashboardService = dashboardService;
    }

    @GetMapping("/attendance")
    public PageResponse<AttendanceResponse> attendance(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "classDate") Pageable pageable) {
        return attendanceService.list(studentId(authentication), null, pageable);
    }

    @GetMapping("/attendance/percentage")
    public AttendancePercentageResponse attendancePercentage(Authentication authentication) {
        return attendanceService.percentageForStudent(studentId(authentication));
    }

    @GetMapping("/marks")
    public PageResponse<MarksResponse> marks(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return marksService.list(studentId(authentication), null, null, pageable);
    }

    @GetMapping("/assignments")
    public PageResponse<SubmissionResponse> assignments(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return submissionService.list(null, studentId(authentication), pageable);
    }

    @GetMapping("/results")
    public PageResponse<ResultResponse> results(
            Authentication authentication,
            @PageableDefault(size = 20, sort = "semester") Pageable pageable) {
        return resultService.list(studentId(authentication), null, pageable);
    }

    @GetMapping("/cgpa")
    public CgpaResponse cgpa(Authentication authentication) {
        return resultService.cgpa(studentId(authentication));
    }

    /** A fresh (not stored) prediction for the logged-in student. */
    @GetMapping("/prediction")
    public PredictionResponse prediction(Authentication authentication) {
        return predictionService.evaluate(student(authentication));
    }

    @GetMapping("/alerts")
    public List<AlertResponse> alerts(Authentication authentication) {
        return alertService.forStudent(studentId(authentication));
    }

    @GetMapping("/analytics")
    public StudentAnalyticsResponse analytics(Authentication authentication) {
        return analyticsService.studentAnalytics(studentId(authentication));
    }

    @GetMapping("/dashboard")
    public StudentDashboardResponse dashboard(Authentication authentication) {
        return dashboardService.studentDashboard(authentication.getName());
    }

    // --------------------------------------------------------------- helpers

    private Student student(Authentication authentication) {
        return studentService.requireEntityByUsername(authentication.getName());
    }

    private Long studentId(Authentication authentication) {
        return student(authentication).getId();
    }
}

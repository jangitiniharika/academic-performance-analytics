package com.academic.performance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.AnalyticsOverviewResponse;
import com.academic.performance.dto.ClassAnalyticsResponse;
import com.academic.performance.dto.StudentAnalyticsResponse;
import com.academic.performance.service.AnalyticsService;

/** Analytics endpoints (Stage 9). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/analytics")
public class AnalyticsController {

    private final AnalyticsService analyticsService;

    public AnalyticsController(AnalyticsService analyticsService) {
        this.analyticsService = analyticsService;
    }

    @GetMapping("/overview")
    public AnalyticsOverviewResponse overview() {
        return analyticsService.overview();
    }

    @GetMapping("/student/{studentId:\\d+}")
    public StudentAnalyticsResponse student(@PathVariable Long studentId) {
        return analyticsService.studentAnalytics(studentId);
    }

    @GetMapping("/class/{subjectId:\\d+}")
    public ClassAnalyticsResponse classAnalytics(@PathVariable Long subjectId) {
        return analyticsService.classAnalytics(subjectId);
    }
}

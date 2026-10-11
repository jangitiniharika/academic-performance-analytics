package com.academic.performance.controller;

import java.util.List;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.AlertResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.entity.AlertSeverity;
import com.academic.performance.service.AlertService;

/** Early-warning alert endpoints (Stage 11). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public PageResponse<AlertResponse> list(
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) AlertSeverity severity,
            @RequestParam(required = false) Boolean acknowledged,
            @PageableDefault(size = 20, sort = "createdAt") Pageable pageable) {
        return alertService.list(studentId, severity, acknowledged, pageable);
    }

    @GetMapping("/student/{studentId:\\d+}")
    public List<AlertResponse> forStudent(@PathVariable Long studentId) {
        return alertService.forStudent(studentId);
    }

    /** Re-runs all alert rules for one student and returns the newly created alerts. */
    @PostMapping("/generate/{studentId:\\d+}")
    public List<AlertResponse> generate(@PathVariable Long studentId) {
        return alertService.generateForStudent(studentId);
    }

    @PatchMapping("/{id:\\d+}/acknowledge")
    public AlertResponse acknowledge(@PathVariable Long id) {
        return alertService.acknowledge(id);
    }
}

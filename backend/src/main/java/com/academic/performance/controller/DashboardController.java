package com.academic.performance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.AdminDashboardResponse;
import com.academic.performance.dto.FacultyDashboardResponse;
import com.academic.performance.service.DashboardService;

/** Admin and Faculty dashboard summaries (Stages 16-17). ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/admin")
    public AdminDashboardResponse admin() {
        return dashboardService.adminDashboard();
    }

    @GetMapping("/faculty")
    public FacultyDashboardResponse faculty() {
        return dashboardService.facultyDashboard();
    }
}

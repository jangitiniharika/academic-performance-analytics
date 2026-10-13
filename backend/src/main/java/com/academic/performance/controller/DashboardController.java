package com.academic.performance.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.AdminDashboardResponse;
import com.academic.performance.dto.FacultyDashboardResponse;
import com.academic.performance.service.AccessScopeService;
import com.academic.performance.service.DashboardService;

/** Admin and Faculty dashboard summaries (Stages 16-17). ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private final DashboardService dashboardService;
    private final AccessScopeService accessScopeService;

    public DashboardController(DashboardService dashboardService,
                               AccessScopeService accessScopeService) {
        this.dashboardService = dashboardService;
        this.accessScopeService = accessScopeService;
    }

    @GetMapping("/admin")
    public AdminDashboardResponse admin() {
        return dashboardService.adminDashboard();
    }

    @GetMapping("/faculty")
    public FacultyDashboardResponse faculty() {
        AccessScopeService.AccessScope scope = accessScopeService.currentScope();
        return dashboardService.facultyDashboard(scope.departmentId(), scope.facultyId());
    }
}

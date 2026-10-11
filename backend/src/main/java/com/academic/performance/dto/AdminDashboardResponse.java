package com.academic.performance.dto;

import java.util.List;

/** Everything the Admin dashboard needs in one call (Stage 17). */
public record AdminDashboardResponse(
        AnalyticsOverviewResponse overview,
        List<RiskStudent> atRiskStudents,
        List<AlertResponse> recentAlerts) {
}

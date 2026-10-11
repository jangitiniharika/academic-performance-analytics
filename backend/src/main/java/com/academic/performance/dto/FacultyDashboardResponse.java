package com.academic.performance.dto;

import java.util.List;

/** Everything the Faculty dashboard needs in one call (Stage 16). */
public record FacultyDashboardResponse(
        long totalStudents,
        long totalSubjects,
        long totalAssignments,
        long pendingSubmissions,
        long openAlerts,
        List<RiskStudent> atRiskStudents,
        List<AlertResponse> recentAlerts) {
}

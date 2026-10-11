package com.academic.performance.dto;

import java.util.List;

/** Everything the Student dashboard needs in one call (Stage 15). */
public record StudentDashboardResponse(
        StudentAnalyticsResponse analytics,
        List<AlertResponse> alerts) {
}

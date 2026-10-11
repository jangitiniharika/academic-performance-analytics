package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.academic.performance.entity.Attendance;

/** Response body describing one {@link Attendance} row. */
public record AttendanceResponse(
        Long id,
        StudentSummary student,
        SubjectSummary subject,
        java.time.LocalDate classDate,
        com.academic.performance.entity.AttendanceStatus status,
        String remarks,
        LocalDateTime createdAt) {

    public static AttendanceResponse of(Attendance attendance) {
        if (attendance == null) {
            return null;
        }
        return new AttendanceResponse(
                attendance.getId(),
                StudentSummary.of(attendance.getStudent()),
                SubjectSummary.of(attendance.getSubject()),
                attendance.getClassDate(),
                attendance.getStatus(),
                attendance.getRemarks(),
                attendance.getCreatedAt());
    }
}

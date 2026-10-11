package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.academic.performance.entity.Marks;

/** Response body describing a {@link Marks} row. */
public record MarksResponse(
        Long id,
        StudentSummary student,
        Long examId,
        String examName,
        SubjectSummary subject,
        Integer maxMarks,
        BigDecimal marksObtained,
        BigDecimal percentage,
        LocalDateTime createdAt) {

    public static MarksResponse of(Marks marks) {
        if (marks == null) {
            return null;
        }
        var exam = marks.getExam();
        return new MarksResponse(
                marks.getId(),
                StudentSummary.of(marks.getStudent()),
                exam == null ? null : exam.getId(),
                exam == null ? null : exam.getName(),
                exam == null ? null : SubjectSummary.of(exam.getSubject()),
                exam == null ? null : exam.getMaxMarks(),
                marks.getMarksObtained(),
                marks.getPercentage(),
                marks.getCreatedAt());
    }
}

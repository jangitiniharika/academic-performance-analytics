package com.academic.performance.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.academic.performance.entity.Exam;

/** Response body describing an {@link Exam}. */
public record ExamResponse(
        Long id,
        String name,
        SubjectSummary subject,
        LocalDate examDate,
        Integer maxMarks,
        LocalDateTime createdAt) {

    public static ExamResponse of(Exam exam) {
        if (exam == null) {
            return null;
        }
        return new ExamResponse(
                exam.getId(),
                exam.getName(),
                SubjectSummary.of(exam.getSubject()),
                exam.getExamDate(),
                exam.getMaxMarks(),
                exam.getCreatedAt());
    }
}

package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.academic.performance.entity.AssignmentSubmission;
import com.academic.performance.entity.SubmissionStatus;

/** Response body describing an {@link AssignmentSubmission}. */
public record SubmissionResponse(
        Long id,
        Long assignmentId,
        String assignmentTitle,
        StudentSummary student,
        SubmissionStatus status,
        BigDecimal marks,
        Integer maxMarks,
        LocalDateTime submittedAt,
        String feedback) {

    public static SubmissionResponse of(AssignmentSubmission submission) {
        if (submission == null) {
            return null;
        }
        var assignment = submission.getAssignment();
        return new SubmissionResponse(
                submission.getId(),
                assignment == null ? null : assignment.getId(),
                assignment == null ? null : assignment.getTitle(),
                StudentSummary.of(submission.getStudent()),
                submission.getStatus(),
                submission.getMarks(),
                assignment == null ? null : assignment.getMaxMarks(),
                submission.getSubmittedAt(),
                submission.getFeedback());
    }
}

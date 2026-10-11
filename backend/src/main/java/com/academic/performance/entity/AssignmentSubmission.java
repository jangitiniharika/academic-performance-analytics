package com.academic.performance.entity;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Tracks one student's submission for one assignment.
 *
 * <p>Relationships:
 * <ul>
 *   <li>{@code @ManyToOne Assignment} - which assignment.</li>
 *   <li>{@code @ManyToOne Student}    - which student.</li>
 * </ul>
 *
 * <p>The unique constraint {@code (assignment_id, student_id)} means a student
 * has at most one submission row per assignment.
 */
@Entity
@Table(
        name = "assignment_submissions",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_submission_assignment_student",
                columnNames = {"assignment_id", "student_id"}),
        indexes = {
                @Index(name = "idx_submissions_assignment", columnList = "assignment_id"),
                @Index(name = "idx_submissions_student", columnList = "student_id")
        })
public class AssignmentSubmission extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assignment_id", nullable = false)
    private Assignment assignment;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 15)
    private SubmissionStatus status = SubmissionStatus.PENDING;

    @DecimalMin(value = "0.00", message = "Assignment marks cannot be negative")
    @Column(name = "marks", precision = 6, scale = 2)
    private BigDecimal marks;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Size(max = 500)
    @Column(name = "feedback", length = 500)
    private String feedback;

    public AssignmentSubmission() {
    }

    /** Stamps the submission time the first time the record is saved as submitted. */
    @PrePersist
    void stampSubmittedAt() {
        if (submittedAt == null && status != SubmissionStatus.PENDING) {
            submittedAt = LocalDateTime.now();
        }
    }

    public Assignment getAssignment() {
        return assignment;
    }

    public void setAssignment(Assignment assignment) {
        this.assignment = assignment;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public SubmissionStatus getStatus() {
        return status;
    }

    public void setStatus(SubmissionStatus status) {
        this.status = status;
    }

    public BigDecimal getMarks() {
        return marks;
    }

    public void setMarks(BigDecimal marks) {
        this.marks = marks;
    }

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public String getFeedback() {
        return feedback;
    }

    public void setFeedback(String feedback) {
        this.feedback = feedback;
    }
}

package com.academic.performance.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import com.academic.performance.service.AcademicCalculator;

/**
 * The marks a single student obtained in a single exam.
 *
 * <p>{@code percentage} is a derived column: it is recalculated automatically
 * before every insert/update by {@link #recalculatePercentage()} using the safe
 * helper {@link AcademicCalculator#marksPercentage}. If the exam's maximum marks
 * are missing the percentage is left {@code null} (missing data is not treated
 * as zero).
 *
 * <p>The unique constraint {@code (student_id, exam_id)} stops the same student
 * being marked twice for one exam.
 */
@Entity
@Table(
        name = "marks",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_marks_student_exam",
                columnNames = {"student_id", "exam_id"}),
        indexes = {
                @Index(name = "idx_marks_student", columnList = "student_id"),
                @Index(name = "idx_marks_exam", columnList = "exam_id")
        })
public class Marks extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "exam_id", nullable = false)
    private Exam exam;

    @NotNull
    @DecimalMin(value = "0.00", message = "Marks obtained cannot be negative")
    @Column(name = "marks_obtained", nullable = false, precision = 6, scale = 2)
    private BigDecimal marksObtained;

    /** Derived: (marksObtained / exam.maxMarks) x 100, or null when unknown. */
    @Column(name = "percentage", precision = 5, scale = 2)
    private BigDecimal percentage;

    public Marks() {
    }

    /** Runs automatically before Hibernate inserts or updates the row. */
    @PrePersist
    @PreUpdate
    void recalculatePercentage() {
        Integer maxMarks = (exam == null) ? null : exam.getMaxMarks();
        this.percentage = AcademicCalculator.marksPercentage(marksObtained, maxMarks).orElse(null);
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Exam getExam() {
        return exam;
    }

    public void setExam(Exam exam) {
        this.exam = exam;
    }

    public BigDecimal getMarksObtained() {
        return marksObtained;
    }

    public void setMarksObtained(BigDecimal marksObtained) {
        this.marksObtained = marksObtained;
    }

    public BigDecimal getPercentage() {
        return percentage;
    }

    public void setPercentage(BigDecimal percentage) {
        this.percentage = percentage;
    }
}

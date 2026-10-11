package com.academic.performance.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A student's final grade for one subject in one semester.
 *
 * <p>These rows are the source of truth for "previous academic performance" and
 * for CGPA: CGPA = sum(gradePoints x credits) / sum(credits), computed in the
 * service layer.
 *
 * <p>Relationships: {@code @ManyToOne Student} and {@code @ManyToOne Subject}.
 * The unique constraint {@code (student_id, subject_id, semester)} prevents the
 * same result being stored twice.
 */
@Entity
@Table(
        name = "academic_results",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_result_student_subject_semester",
                columnNames = {"student_id", "subject_id", "semester"}),
        indexes = {
                @Index(name = "idx_results_student", columnList = "student_id"),
                @Index(name = "idx_results_subject", columnList = "subject_id")
        })
public class AcademicResult extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "semester", nullable = false)
    private Integer semester;

    @NotBlank
    @Size(max = 3)
    @Column(name = "grade", nullable = false, length = 3)
    private String grade;

    @NotNull
    @DecimalMin("0.00")
    @DecimalMax("10.00")
    @Column(name = "grade_points", nullable = false, precision = 4, scale = 2)
    private BigDecimal gradePoints;

    @NotNull
    @Min(0)
    @Max(20)
    @Column(name = "credits", nullable = false)
    private Integer credits;

    public AcademicResult() {
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public BigDecimal getGradePoints() {
        return gradePoints;
    }

    public void setGradePoints(BigDecimal gradePoints) {
        this.gradePoints = gradePoints;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }
}

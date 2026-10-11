package com.academic.performance.entity;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

/**
 * One attendance record: "student X was PRESENT/ABSENT/LATE for subject Y on date Z".
 *
 * <p>The unique constraint {@code (student_id, subject_id, class_date)} prevents
 * recording the same student twice for the same subject on the same day.
 *
 * <p>Attendance percentage is NOT stored here (it is a per-student aggregate).
 * It is computed in the service layer with
 * {@code AcademicCalculator.attendancePercentage(...)} using the repository
 * count methods, e.g. {@code countByStudentIdAndStatus(...)}.
 */
@Entity
@Table(
        name = "attendance",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_attendance_student_subject_date",
                columnNames = {"student_id", "subject_id", "class_date"}),
        indexes = {
                @Index(name = "idx_attendance_student", columnList = "student_id"),
                @Index(name = "idx_attendance_subject", columnList = "subject_id")
        })
public class Attendance extends BaseEntity {

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private Student student;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotNull
    @PastOrPresent
    @Column(name = "class_date", nullable = false)
    private LocalDate classDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 10)
    private AttendanceStatus status;

    @Size(max = 255)
    @Column(name = "remarks", length = 255)
    private String remarks;

    public Attendance() {
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

    public LocalDate getClassDate() {
        return classDate;
    }

    public void setClassDate(LocalDate classDate) {
        this.classDate = classDate;
    }

    public AttendanceStatus getStatus() {
        return status;
    }

    public void setStatus(AttendanceStatus status) {
        this.status = status;
    }

    public String getRemarks() {
        return remarks;
    }

    public void setRemarks(String remarks) {
        this.remarks = remarks;
    }
}

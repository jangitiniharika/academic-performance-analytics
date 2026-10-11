package com.academic.performance.entity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * An examination belonging to a subject (for example "Mid-Term 1").
 *
 * <p>Relationships:
 * <ul>
 *   <li>{@code @ManyToOne Subject} - which subject the exam is for.</li>
 *   <li>{@code @OneToMany Marks}   - all marks rows recorded for this exam.
 *       The collection is used for cascade deletes (deleting an exam deletes its
 *       marks) and is marked {@code @JsonIgnore} so it can never cause a circular
 *       JSON reference.</li>
 * </ul>
 */
@Entity
@Table(
        name = "exams",
        indexes = @Index(name = "idx_exams_subject", columnList = "subject_id"))
public class Exam extends BaseEntity {

    @NotBlank
    @Size(max = 100)
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @NotNull
    @Column(name = "exam_date", nullable = false)
    private LocalDate examDate;

    @NotNull
    @Min(1)
    @Max(1000)
    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @JsonIgnore
    @OneToMany(mappedBy = "exam", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Marks> marks = new ArrayList<>();

    public Exam() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public LocalDate getExamDate() {
        return examDate;
    }

    public void setExamDate(LocalDate examDate) {
        this.examDate = examDate;
    }

    public Integer getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Integer maxMarks) {
        this.maxMarks = maxMarks;
    }

    public List<Marks> getMarks() {
        return marks;
    }

    public void setMarks(List<Marks> marks) {
        this.marks = marks;
    }
}

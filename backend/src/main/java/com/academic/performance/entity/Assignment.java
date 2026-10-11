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
 * A piece of coursework set for a subject (for example "Unit 3 assignment").
 *
 * <p>Relationships:
 * <ul>
 *   <li>{@code @ManyToOne Subject} - the subject the assignment belongs to.</li>
 *   <li>{@code @ManyToOne Faculty} - who created it (optional).</li>
 *   <li>{@code @OneToMany AssignmentSubmission} - submissions received. The
 *       collection exists so that deleting an assignment also deletes its
 *       submissions (cascade + orphan removal) and is {@code @JsonIgnore}d to
 *       prevent circular JSON.</li>
 * </ul>
 */
@Entity
@Table(
        name = "assignments",
        indexes = {
                @Index(name = "idx_assignments_subject", columnList = "subject_id"),
                @Index(name = "idx_assignments_faculty", columnList = "faculty_id")
        })
public class Assignment extends BaseEntity {

    @NotBlank
    @Size(max = 150)
    @Column(name = "title", nullable = false, length = 150)
    private String title;

    @Size(max = 1000)
    @Column(name = "description", length = 1000)
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "subject_id", nullable = false)
    private Subject subject;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    @NotNull
    @Column(name = "due_date", nullable = false)
    private LocalDate dueDate;

    @NotNull
    @Min(1)
    @Max(1000)
    @Column(name = "max_marks", nullable = false)
    private Integer maxMarks;

    @JsonIgnore
    @OneToMany(mappedBy = "assignment", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<AssignmentSubmission> submissions = new ArrayList<>();

    public Assignment() {
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Subject getSubject() {
        return subject;
    }

    public void setSubject(Subject subject) {
        this.subject = subject;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public Integer getMaxMarks() {
        return maxMarks;
    }

    public void setMaxMarks(Integer maxMarks) {
        this.maxMarks = maxMarks;
    }

    public List<AssignmentSubmission> getSubmissions() {
        return submissions;
    }

    public void setSubmissions(List<AssignmentSubmission> submissions) {
        this.submissions = submissions;
    }
}

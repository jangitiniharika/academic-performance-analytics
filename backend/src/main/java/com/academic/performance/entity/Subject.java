package com.academic.performance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A subject taught in a department (for example "Database Management Systems").
 *
 * <p>Relationships:
 * <ul>
 *   <li>{@code @ManyToOne Department} - the department that offers the subject.</li>
 *   <li>{@code @ManyToOne Faculty}   - the faculty member who teaches it (optional).</li>
 * </ul>
 *
 * <p>{@code code} is unique (for example {@code CS301}).
 */
@Entity
@Table(
        name = "subjects",
        indexes = {
                @Index(name = "idx_subjects_department", columnList = "department_id"),
                @Index(name = "idx_subjects_faculty", columnList = "faculty_id")
        })
public class Subject extends BaseEntity {

    @NotBlank
    @Size(max = 15)
    @Column(name = "code", nullable = false, unique = true, length = 15)
    private String code;

    @NotBlank
    @Size(max = 120)
    @Column(name = "name", nullable = false, length = 120)
    private String name;

    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "semester", nullable = false)
    private Integer semester;

    @NotNull
    @Min(1)
    @Max(20)
    @Column(name = "credits", nullable = false)
    private Integer credits;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "faculty_id")
    private Faculty faculty;

    public Subject() {
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getSemester() {
        return semester;
    }

    public void setSemester(Integer semester) {
        this.semester = semester;
    }

    public Integer getCredits() {
        return credits;
    }

    public void setCredits(Integer credits) {
        this.credits = credits;
    }

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public Faculty getFaculty() {
        return faculty;
    }

    public void setFaculty(Faculty faculty) {
        this.faculty = faculty;
    }
}

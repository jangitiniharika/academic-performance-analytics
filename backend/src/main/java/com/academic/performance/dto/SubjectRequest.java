package com.academic.performance.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating/updating a {@link com.academic.performance.entity.Subject}.
 *
 * <p>{@code departmentId} is required; {@code facultyId} is optional (a subject may
 * exist before a teacher is assigned).
 */
public class SubjectRequest {

    @NotBlank(message = "Subject code is required.")
    @Size(max = 15, message = "Subject code must be at most 15 characters.")
    private String code;

    @NotBlank(message = "Subject name is required.")
    @Size(max = 120, message = "Subject name must be at most 120 characters.")
    private String name;

    @NotNull(message = "Semester is required.")
    @Min(value = 1, message = "Semester must be between 1 and 12.")
    @Max(value = 12, message = "Semester must be between 1 and 12.")
    private Integer semester;

    @NotNull(message = "Credits are required.")
    @Min(value = 1, message = "Credits must be between 1 and 20.")
    @Max(value = 20, message = "Credits must be between 1 and 20.")
    private Integer credits;

    @NotNull(message = "Department id is required.")
    private Long departmentId;

    /** Optional: the faculty member who teaches this subject. */
    private Long facultyId;

    public SubjectRequest() {
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

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }
}

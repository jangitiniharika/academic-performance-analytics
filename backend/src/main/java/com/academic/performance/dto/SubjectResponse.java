package com.academic.performance.dto;

import java.time.LocalDateTime;

/** Response body describing a {@link com.academic.performance.entity.Subject}. */
public class SubjectResponse {

    private Long id;
    private String code;
    private String name;
    private Integer semester;
    private Integer credits;
    private DepartmentSummary department;
    private Long facultyId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public SubjectResponse() {
    }

    public SubjectResponse(Long id, String code, String name, Integer semester, Integer credits,
                           DepartmentSummary department, Long facultyId,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.semester = semester;
        this.credits = credits;
        this.department = department;
        this.facultyId = facultyId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public DepartmentSummary getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentSummary department) {
        this.department = department;
    }

    public Long getFacultyId() {
        return facultyId;
    }

    public void setFacultyId(Long facultyId) {
        this.facultyId = facultyId;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}

package com.academic.performance.dto;

/**
 * A compact view of a department, embedded inside Subject/Student/Faculty
 * responses.
 *
 * <p>Using a small summary (instead of the full Department object) keeps nested
 * JSON shallow and avoids accidentally exposing fields the client does not need.
 */
public class DepartmentSummary {

    private Long id;
    private String name;
    private String code;

    public DepartmentSummary() {
    }

    public DepartmentSummary(Long id, String name, String code) {
        this.id = id;
        this.name = name;
        this.code = code;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}

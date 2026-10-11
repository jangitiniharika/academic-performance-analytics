package com.academic.performance.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating/updating a {@link com.academic.performance.entity.Department}.
 *
 * <p>Bean Validation rejects blank names/codes and over-long values with a 400,
 * before the service runs.
 */
public class DepartmentRequest {

    @NotBlank(message = "Department name is required.")
    @Size(max = 100, message = "Department name must be at most 100 characters.")
    private String name;

    @NotBlank(message = "Department code is required.")
    @Size(max = 10, message = "Department code must be at most 10 characters.")
    private String code;

    @Size(max = 255, message = "Description must be at most 255 characters.")
    private String description;

    public DepartmentRequest() {
    }

    public DepartmentRequest(String name, String code, String description) {
        this.name = name;
        this.code = code;
        this.description = description;
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

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}

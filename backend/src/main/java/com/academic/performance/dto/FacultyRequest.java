package com.academic.performance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating/updating a {@link com.academic.performance.entity.Faculty}.
 *
 * <p>{@code userId} is optional (links the faculty record to a login account).
 */
public class FacultyRequest {

    @NotBlank(message = "Employee code is required.")
    @Size(max = 30, message = "Employee code must be at most 30 characters.")
    private String employeeCode;

    @NotBlank(message = "First name is required.")
    @Size(max = 60, message = "First name must be at most 60 characters.")
    private String firstName;

    @NotBlank(message = "Last name is required.")
    @Size(max = 60, message = "Last name must be at most 60 characters.")
    private String lastName;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid address.")
    @Size(max = 120, message = "Email must be at most 120 characters.")
    private String email;

    @Size(max = 20, message = "Phone must be at most 20 characters.")
    private String phone;

    @Size(max = 100, message = "Designation must be at most 100 characters.")
    private String designation;

    @NotNull(message = "Department id is required.")
    private Long departmentId;

    /** Optional link to an existing login account. */
    private Long userId;

    public FacultyRequest() {
    }

    public String getEmployeeCode() {
        return employeeCode;
    }

    public void setEmployeeCode(String employeeCode) {
        this.employeeCode = employeeCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public Long getDepartmentId() {
        return departmentId;
    }

    public void setDepartmentId(Long departmentId) {
        this.departmentId = departmentId;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }
}

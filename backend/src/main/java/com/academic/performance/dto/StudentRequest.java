package com.academic.performance.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Request body for creating/updating a {@link com.academic.performance.entity.Student}.
 *
 * <p>{@code userId} is optional: it lets an administrator link the student record
 * to an existing login account. When omitted the student simply has no login yet.
 */
public class StudentRequest {

    @NotBlank(message = "Roll number is required.")
    @Size(max = 30, message = "Roll number must be at most 30 characters.")
    private String rollNumber;

    @NotBlank(message = "Registration number is required.")
    @Size(max = 30, message = "Registration number must be at most 30 characters.")
    private String registrationNumber;

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

    @NotNull(message = "Current semester is required.")
    @Min(value = 1, message = "Semester must be between 1 and 12.")
    @Max(value = 12, message = "Semester must be between 1 and 12.")
    private Integer currentSemester;

    @DecimalMin(value = "0.00", message = "CGPA must be between 0 and 10.")
    @DecimalMax(value = "10.00", message = "CGPA must be between 0 and 10.")
    private BigDecimal cgpa;

    @Min(value = 1900, message = "Admission year must be 1900 or later.")
    private Integer admissionYear;

    @NotNull(message = "Department id is required.")
    private Long departmentId;

    /** Optional link to an existing login account. */
    private Long userId;

    public StudentRequest() {
    }

    public String getRollNumber() {
        return rollNumber;
    }

    public void setRollNumber(String rollNumber) {
        this.rollNumber = rollNumber;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
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

    public Integer getCurrentSemester() {
        return currentSemester;
    }

    public void setCurrentSemester(Integer currentSemester) {
        this.currentSemester = currentSemester;
    }

    public BigDecimal getCgpa() {
        return cgpa;
    }

    public void setCgpa(BigDecimal cgpa) {
        this.cgpa = cgpa;
    }

    public Integer getAdmissionYear() {
        return admissionYear;
    }

    public void setAdmissionYear(Integer admissionYear) {
        this.admissionYear = admissionYear;
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

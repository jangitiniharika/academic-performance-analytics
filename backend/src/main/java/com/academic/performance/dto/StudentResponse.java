package com.academic.performance.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Response body describing a {@link com.academic.performance.entity.Student}.
 *
 * <p>The linked login account is represented only by {@code userId}; no credentials
 * are ever included.
 */
public class StudentResponse {

    private Long id;
    private String rollNumber;
    private String registrationNumber;
    private String firstName;
    private String lastName;
    private String fullName;
    private String email;
    private String phone;
    private Integer currentSemester;
    private BigDecimal cgpa;
    private Integer admissionYear;
    private DepartmentSummary department;
    private Long userId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public StudentResponse() {
    }

    public StudentResponse(Long id, String rollNumber, String registrationNumber, String firstName,
                           String lastName, String fullName, String email, String phone,
                           Integer currentSemester, BigDecimal cgpa, Integer admissionYear,
                           DepartmentSummary department, Long userId,
                           LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.rollNumber = rollNumber;
        this.registrationNumber = registrationNumber;
        this.firstName = firstName;
        this.lastName = lastName;
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.currentSemester = currentSemester;
        this.cgpa = cgpa;
        this.admissionYear = admissionYear;
        this.department = department;
        this.userId = userId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public DepartmentSummary getDepartment() {
        return department;
    }

    public void setDepartment(DepartmentSummary department) {
        this.department = department;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
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

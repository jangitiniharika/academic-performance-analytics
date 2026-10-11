package com.academic.performance.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A student enrolled in a department.
 *
 * <p>Relationships:
 * <ul>
 *   <li>{@code @ManyToOne Department} - every student belongs to one department.</li>
 *   <li>{@code @OneToOne User} - links the student to their login account.
 *       The join column is unique, so two students cannot share an account.</li>
 * </ul>
 *
 * <p>{@code rollNumber}, {@code registrationNumber} and {@code email} are all
 * unique so duplicate student records cannot be created.
 */
@Entity
@Table(
        name = "students",
        indexes = @Index(name = "idx_students_department", columnList = "department_id"))
public class Student extends BaseEntity {

    @NotBlank
    @Size(max = 30)
    @Column(name = "roll_number", nullable = false, unique = true, length = 30)
    private String rollNumber;

    @NotBlank
    @Size(max = 30)
    @Column(name = "registration_number", nullable = false, unique = true, length = 30)
    private String registrationNumber;

    @NotBlank
    @Size(max = 60)
    @Column(name = "first_name", nullable = false, length = 60)
    private String firstName;

    @NotBlank
    @Size(max = 60)
    @Column(name = "last_name", nullable = false, length = 60)
    private String lastName;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    @Size(max = 20)
    @Column(name = "phone", length = 20)
    private String phone;

    @NotNull
    @Min(1)
    @Max(12)
    @Column(name = "current_semester", nullable = false)
    private Integer currentSemester;

    /** CGPA on a 10-point scale. Null until enough results exist. */
    @DecimalMin("0.00")
    @DecimalMax("10.00")
    @Column(name = "cgpa", precision = 4, scale = 2)
    private BigDecimal cgpa;

    @Min(1900)
    @Column(name = "admission_year")
    private Integer admissionYear;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Student() {
    }

    /** Convenience: full name built from first + last name. */
    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
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

    public Department getDepartment() {
        return department;
    }

    public void setDepartment(Department department) {
        this.department = department;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}

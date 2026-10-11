package com.academic.performance.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * A faculty (teaching staff) member.
 *
 * <p>Relationships:
 * <ul>
 *   <li>{@code @ManyToOne Department} - the department they belong to.</li>
 *   <li>{@code @OneToOne User} - their login account (unique).</li>
 * </ul>
 *
 * <p>{@code employeeCode} and {@code email} are unique.
 */
@Entity
@Table(
        name = "faculty",
        indexes = @Index(name = "idx_faculty_department", columnList = "department_id"))
public class Faculty extends BaseEntity {

    @NotBlank
    @Size(max = 30)
    @Column(name = "employee_code", nullable = false, unique = true, length = 30)
    private String employeeCode;

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

    @Size(max = 100)
    @Column(name = "designation", length = 100)
    private String designation;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "department_id", nullable = false)
    private Department department;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true)
    private User user;

    public Faculty() {
    }

    public String getFullName() {
        return (firstName == null ? "" : firstName) + " " + (lastName == null ? "" : lastName);
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

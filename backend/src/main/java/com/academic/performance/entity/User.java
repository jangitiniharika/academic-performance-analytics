package com.academic.performance.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * Login account for every person who uses the system (Admin, Faculty, Student).
 *
 * <p>Security decisions:
 * <ul>
 *   <li>{@code passwordHash} stores ONLY the BCrypt hash produced by
 *       {@code BCryptPasswordEncoder}. Plain-text passwords are never stored.</li>
 *   <li>The field is annotated with {@code @JsonIgnore} so even if a User object
 *       were ever serialised by mistake, the hash could not leak to a client.</li>
 *   <li>{@code username} and {@code email} are unique - the database rejects
 *       duplicates.</li>
 * </ul>
 *
 * <p>Table is named {@code users} because {@code user} is a reserved word in MySQL.
 */
@Entity
@Table(name = "users")
public class User extends BaseEntity {

    @NotBlank
    @Size(max = 50)
    @Column(name = "username", nullable = false, unique = true, length = 50)
    private String username;

    @NotBlank
    @Email
    @Size(max = 120)
    @Column(name = "email", nullable = false, unique = true, length = 120)
    private String email;

    /** BCrypt hash - never the original password. */
    @JsonIgnore
    @NotBlank
    @Size(max = 100)
    @Column(name = "password_hash", nullable = false, length = 100)
    private String passwordHash;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "role", nullable = false, length = 20)
    private Role role;

    @Column(name = "enabled", nullable = false)
    private boolean enabled = true;

    public User() {
    }

    public User(String username, String email, String passwordHash, Role role) {
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}

package com.academic.performance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/**
 * Request body for {@code POST /api/auth/login}.
 *
 * <p>Users log in with their email and password. The raw password is compared
 * against the stored BCrypt hash using {@code PasswordEncoder.matches(...)}; the
 * plain text is never stored or logged.
 */
public class LoginRequest {

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid address.")
    private String email;

    @NotBlank(message = "Password is required.")
    private String password;

    public LoginRequest() {
    }

    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

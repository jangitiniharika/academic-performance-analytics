package com.academic.performance.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request body for {@code POST /api/auth/register}.
 *
 * <p>Bean Validation annotations are checked automatically because the
 * controller marks the parameter with {@code @Valid}. If any rule fails, Spring
 * returns a 400 response listing the offending fields - the service is never
 * called with bad data.
 *
 * <p>Note: the public registration endpoint always creates a STUDENT account.
 * ADMIN and FACULTY accounts are provisioned by an administrator (later stage)
 * or by the development seeder, so nobody can self-promote by editing the
 * request body.
 */
public class RegisterRequest {

    @NotBlank(message = "Username is required.")
    @Size(min = 3, max = 50, message = "Username must be between 3 and 50 characters.")
    private String username;

    @NotBlank(message = "Email is required.")
    @Email(message = "Email must be a valid address.")
    @Size(max = 120, message = "Email must be at most 120 characters.")
    private String email;

    /**
     * Plain-text password in transit (HTTPS in production). Maximum 72 because
     * BCrypt only uses the first 72 bytes - longer input would be silently
     * truncated, so we reject it instead.
     */
    @NotBlank(message = "Password is required.")
    @Size(min = 8, max = 72, message = "Password must be between 8 and 72 characters.")
    private String password;

    public RegisterRequest() {
    }

    public RegisterRequest(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
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

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

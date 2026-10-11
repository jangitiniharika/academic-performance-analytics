package com.academic.performance.dto;

import com.academic.performance.entity.Role;

/**
 * Public representation of a user account.
 *
 * <p>This DTO deliberately contains <strong>no password or hash field</strong>.
 * Entities are never returned directly from the authentication endpoints, so
 * there is no way for a password hash to leak into a response.
 */
public class UserResponse {

    private Long id;
    private String username;
    private String email;
    private Role role;
    private boolean enabled;

    public UserResponse() {
    }

    public UserResponse(Long id, String username, String email, Role role, boolean enabled) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.role = role;
        this.enabled = enabled;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

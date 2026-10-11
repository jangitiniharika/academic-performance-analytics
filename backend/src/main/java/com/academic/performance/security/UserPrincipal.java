package com.academic.performance.security;

import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;

/**
 * Spring Security's view of a logged-in {@link User}.
 *
 * <p>Spring Security never works with our JPA entity directly; it works with a
 * {@link UserDetails}. This small adapter wraps the entity and exposes exactly
 * what the framework needs:
 * <ul>
 *   <li>{@code getUsername()}  - the login name (the JWT subject),</li>
 *   <li>{@code getPassword()}  - the BCrypt hash (never the plain password),</li>
 *   <li>{@code getAuthorities()} - e.g. {@code ROLE_ADMIN}, which is what
 *       {@code hasRole("ADMIN")} checks.</li>
 * </ul>
 *
 * <p>The wrapped entity is available through {@link #getUser()} so controllers
 * can read the id/email/role without another database call.
 */
public class UserPrincipal implements UserDetails {

    private final User user;

    public UserPrincipal(User user) {
        this.user = user;
    }

    public static UserPrincipal from(User user) {
        return new UserPrincipal(user);
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // "ROLE_" prefix is required for hasRole("ADMIN") to match.
        return List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));
    }

    @Override
    public String getPassword() {
        return user.getPasswordHash();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    public User getUser() {
        return user;
    }

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public Role getRole() {
        return user.getRole();
    }
}

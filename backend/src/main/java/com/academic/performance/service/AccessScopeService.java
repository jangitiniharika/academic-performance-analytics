package com.academic.performance.service;

import java.util.Optional;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.entity.Faculty;
import com.academic.performance.repository.FacultyRepository;

/**
 * Resolves the <em>data scope</strong> of the signed-in user.
 *
 * <p>Role-based access control ({@code SecurityConfig}) decides which endpoints a
 * user may call. This service adds the second half of the rule: a FACULTY member
 * only sees the students of their own department, the subjects they teach, the
 * assignments they created and the alerts raised for their students.
 *
 * <p>An ADMIN (and a FACULTY account that has no linked profile yet) is
 * <strong>not</strong> scoped, so nothing is hidden from an administrator. The
 * scope is derived from the authenticated username only - it is never accepted
 * from the client, so it cannot be spoofed.
 */
@Service
public class AccessScopeService {

    private final FacultyRepository facultyRepository;

    public AccessScopeService(FacultyRepository facultyRepository) {
        this.facultyRepository = facultyRepository;
    }

    /**
     * An optional data filter applied on top of role-based access control.
     * A {@code null} field means "no restriction on this dimension".
     */
    public record AccessScope(Long departmentId, Long facultyId) {

        public static final AccessScope UNSCOPED = new AccessScope(null, null);

        public boolean isScoped() {
            return departmentId != null || facultyId != null;
        }
    }

    /** The data scope for the current request (never {@code null}). */
    @Transactional(readOnly = true)
    public AccessScope currentScope() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return AccessScope.UNSCOPED;
        }

        // Only a pure FACULTY account is scoped; administrators see everything.
        if (hasAuthority(authentication, "ROLE_ADMIN")
                || !hasAuthority(authentication, "ROLE_FACULTY")) {
            return AccessScope.UNSCOPED;
        }

        String username = authentication.getName();
        if (username == null || username.isBlank() || "anonymousUser".equals(username)) {
            return AccessScope.UNSCOPED;
        }

        // A faculty account without a linked profile has nothing to scope by, so
        // it keeps the role-based (unscoped) view rather than locking itself out.
        Optional<Faculty> faculty = facultyRepository.findByUser_Username(username);
        return faculty
                .map(profile -> new AccessScope(
                        profile.getDepartment() == null ? null : profile.getDepartment().getId(),
                        profile.getId()))
                .orElse(AccessScope.UNSCOPED);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        for (GrantedAuthority granted : authentication.getAuthorities()) {
            if (authority.equals(granted.getAuthority())) {
                return true;
            }
        }
        return false;
    }
}

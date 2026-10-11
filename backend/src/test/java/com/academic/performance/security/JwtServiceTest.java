package com.academic.performance.security;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;
import org.springframework.security.core.userdetails.UserDetails;

import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;

/**
 * Pure unit test (no Spring, no database) for the JWT signing/verification logic.
 * It proves that only tokens signed with our secret and still in date are accepted.
 */
class JwtServiceTest {

    private static final String SECRET = "unit-test-secret-0123456789abcdefghijklmnop";

    private JwtService jwtService(long expirationMs) {
        return new JwtService(SECRET, expirationMs);
    }

    private UserDetails userDetails() {
        User user = new User("alice", "alice@campus.edu", "irrelevant-hash", Role.ADMIN);
        user.setId(42L);
        return new UserPrincipal(user);
    }

    @Test
    void generatesTokenAndExtractsUsername() {
        JwtService service = jwtService(60_000);

        String token = service.generateToken(userDetails());

        assertThat(token).isNotBlank();
        assertThat(service.extractUsername(token)).isEqualTo("alice");
        assertThat(service.isTokenValid(token, userDetails())).isTrue();
    }

    @Test
    void tokenCarriesTheUsersRoleClaim() {
        String token = jwtService(60_000).generateToken(userDetails());

        assertThat(jwtService(60_000).extractRoles(token)).containsExactly("ROLE_ADMIN");
    }

    @Test
    void rejectsTokenSignedWithADifferentSecret() {
        JwtService attacker = new JwtService("attacker-secret-0123456789abcdefghijklmnop", 60_000);
        String forged = attacker.generateToken(userDetails());

        assertThat(jwtService(60_000).isTokenValid(forged, userDetails())).isFalse();
    }

    @Test
    void rejectsExpiredToken() {
        String expired = jwtService(-1_000).generateToken(userDetails());

        assertThat(jwtService(60_000).isTokenValid(expired, userDetails())).isFalse();
    }

    @Test
    void rejectsTokenIssuedForADifferentUser() {
        String token = jwtService(60_000).generateToken(userDetails());

        User other = new User("bob", "bob@campus.edu", "hash", Role.STUDENT);
        other.setId(7L);

        assertThat(jwtService(60_000).isTokenValid(token, new UserPrincipal(other))).isFalse();
    }

    @Test
    void refusesToStartWithATooShortSecret() {
        assertThatThrownBy(() -> new JwtService("too-short", 60_000))
                .isInstanceOf(IllegalStateException.class);
    }
}

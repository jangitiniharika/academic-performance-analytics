package com.academic.performance.security;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

/**
 * Creates and verifies JSON Web Tokens (JWT) using the JJWT library.
 *
 * <p>A token is a short string the client sends back on every request as
 * {@code Authorization: Bearer <token>}. It is digitally signed with a secret
 * key, so the server can trust its contents without storing a session.
 *
 * <p>The signing secret is read from the environment/property
 * {@code app.jwt.secret} and is NEVER returned in an API response. A key shorter
 * than 256 bits (32 bytes) is rejected at startup, because HS256 would be weak.
 */
@Service
public class JwtService {

    /** Minimum key length for HS256, enforced by JJWT. */
    private static final int MIN_SECRET_BYTES = 32;

    private final SecretKey signingKey;
    private final long expirationMs;

    public JwtService(@Value("${app.jwt.secret}") String secret,
                      @Value("${app.jwt.expiration-ms}") long expirationMs) {
        byte[] keyBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "app.jwt.secret must be at least " + MIN_SECRET_BYTES + " bytes for HS256 signing.");
        }
        this.signingKey = Keys.hmacShaKeyFor(keyBytes);
        this.expirationMs = expirationMs;
    }

    /** Builds a signed token for an authenticated user. */
    public String generateToken(UserDetails userDetails) {
        Instant now = Instant.now();
        Instant expiry = now.plusMillis(expirationMs);

        JwtBuilder builder = Jwts.builder()
                .subject(userDetails.getUsername())
                .claim("roles", userDetails.getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority)
                        .toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(expiry))
                .signWith(signingKey);

        if (userDetails instanceof UserPrincipal principal) {
            builder.claim("uid", principal.getId())
                    .claim("role", principal.getRole().name())
                    .claim("email", principal.getEmail());
        }

        return builder.compact();
    }

    /** Reads the subject (username) from a token. */
    public String extractUsername(String token) {
        return parseClaims(token).getSubject();
    }

    /** Reads all roles stored in the token (handy for debugging/inspection). */
    @SuppressWarnings("unchecked")
    public List<String> extractRoles(String token) {
        Object roles = parseClaims(token).get("roles");
        return (roles instanceof List<?> list) ? (List<String>) list : List.of();
    }

    /**
     * True when the token's signature is valid, it has not expired, and it
     * belongs to the given user.
     */
    public boolean isTokenValid(String token, UserDetails userDetails) {
        try {
            Claims claims = parseClaims(token);
            boolean sameUser = claims.getSubject().equals(userDetails.getUsername());
            boolean notExpired = claims.getExpiration().after(new Date());
            return sameUser && notExpired;
        } catch (JwtException | IllegalArgumentException ex) {
            // ExpiredJwtException, SignatureException, malformed token, ...
            return false;
        }
    }

    public long getExpirationMs() {
        return expirationMs;
    }

    /** Token lifetime in seconds - returned to the client so it knows when to re-login. */
    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    private Claims parseClaims(String token) {
        return Jwts.parser()
                .verifyWith(signingKey)
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }
}

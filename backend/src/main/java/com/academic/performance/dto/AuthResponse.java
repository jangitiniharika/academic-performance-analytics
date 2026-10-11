package com.academic.performance.dto;

/**
 * Response body for a successful {@code POST /api/auth/login}.
 *
 * <p>Contains the signed JWT the client must send back as
 * {@code Authorization: Bearer <token>}, plus a small amount of non-sensitive
 * user info so the React app can show the right dashboard.
 *
 * <p>Example:
 * <pre>
 * {
 *   "token": "eyJhbGciOiJIUzI1NiJ9...",
 *   "tokenType": "Bearer",
 *   "expiresIn": 86400,
 *   "user": { "id": 1, "username": "student1", "email": "student1@campus.edu",
 *             "role": "STUDENT", "enabled": true }
 * }
 * </pre>
 */
public class AuthResponse {

    private String token;
    private String tokenType;
    /** Token lifetime in seconds. */
    private long expiresIn;
    private UserResponse user;

    public AuthResponse() {
    }

    public AuthResponse(String token, String tokenType, long expiresIn, UserResponse user) {
        this.token = token;
        this.tokenType = tokenType;
        this.expiresIn = expiresIn;
        this.user = user;
    }

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }
}

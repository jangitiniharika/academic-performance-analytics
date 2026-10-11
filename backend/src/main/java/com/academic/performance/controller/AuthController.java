package com.academic.performance.controller;

import jakarta.validation.Valid;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.AuthResponse;
import com.academic.performance.dto.LoginRequest;
import com.academic.performance.dto.RegisterRequest;
import com.academic.performance.dto.UserResponse;
import com.academic.performance.service.AuthService;

/**
 * Public authentication endpoints.
 *
 * <table border="1">
 *   <caption>Endpoints</caption>
 *   <tr><th>Method</th><th>Path</th><th>Body</th><th>Success</th></tr>
 *   <tr><td>POST</td><td>/api/auth/register</td><td>RegisterRequest</td><td>201 + UserResponse</td></tr>
 *   <tr><td>POST</td><td>/api/auth/login</td><td>LoginRequest</td><td>200 + AuthResponse (JWT)</td></tr>
 *   <tr><td>GET</td><td>/api/auth/me</td><td>Bearer token</td><td>200 + UserResponse</td></tr>
 * </table>
 *
 * <p>{@code register} and {@code login} are whitelisted in {@code SecurityConfig};
 * {@code /api/auth/me} requires a valid token.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> register(@Valid @RequestBody RegisterRequest request) {
        UserResponse created = authService.register(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponse> currentUser(Authentication authentication) {
        return ResponseEntity.ok(authService.currentUser(authentication.getName()));
    }
}

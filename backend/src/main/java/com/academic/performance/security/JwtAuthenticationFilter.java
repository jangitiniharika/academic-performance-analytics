package com.academic.performance.security;

import java.io.IOException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

/**
 * Runs once on every request and tries to authenticate the caller from the
 * {@code Authorization: Bearer <token>} header.
 *
 * <p>Flow:
 * <ol>
 *   <li>Read the header. If it is missing or does not start with "Bearer ",
 *       do nothing - the request simply continues as anonymous.</li>
 *   <li>Extract the username (subject) from the signed token.</li>
 *   <li>Load the user and confirm the token signature, expiry and subject are
 *       all valid.</li>
 *   <li>Put an {@link UsernamePasswordAuthenticationToken} into the
 *       {@link SecurityContextHolder}, which is how the rest of Spring Security
 *       knows "who" is calling.</li>
 * </ol>
 *
 * <p>An invalid or tampered token never throws out of the filter - it just leaves
 * the request anonymous, and the entry point turns that into a clean 401 for
 * protected URLs.
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final AppUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtService jwtService, AppUserDetailsService userDetailsService) {
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@NonNull HttpServletRequest request,
                                    @NonNull HttpServletResponse response,
                                    @NonNull FilterChain filterChain)
            throws ServletException, IOException {

        String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(BEARER_PREFIX)) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authHeader.substring(BEARER_PREFIX.length());

        try {
            String username = jwtService.extractUsername(token);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtService.isTokenValid(token, userDetails)) {
                    UsernamePasswordAuthenticationToken authentication =
                            new UsernamePasswordAuthenticationToken(
                                    userDetails, null, userDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }
        } catch (JwtException | AuthenticationException | IllegalArgumentException ex) {
            // Token is expired, tampered with, or references a deleted user.
            // Stay anonymous; protected endpoints will answer with 401.
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}

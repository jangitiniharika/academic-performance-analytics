package com.academic.performance.security;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

/**
 * Central Spring Security configuration for the whole application.
 *
 * <p>Stage 3 responsibilities:
 * <ul>
 *   <li>Keep the API <strong>stateless</strong> - no HTTP session; every request
 *       carries its own JWT.</li>
 *   <li>Register the {@link JwtAuthenticationFilter} which authenticates
 *       requests from the {@code Authorization: Bearer <token>} header.</li>
 *   <li>Provide the {@link DaoAuthenticationProvider} that checks email/password
 *       against the {@code users} table using BCrypt.</li>
 *   <li>Authorize by role (extended in Stage 4):
 *       <ul>
 *         <li>{@code /api/auth/register} and {@code /api/auth/login}, {@code /api/health} - public,</li>
 *         <li>GET {@code /api/departments/**}, {@code /api/subjects/**} - any authenticated user,</li>
 *         <li>GET {@code /api/students/**}, {@code /api/faculty/**} - ADMIN or FACULTY,</li>
 *         <li>write methods on those resources        - ADMIN only,</li>
 *         <li>{@code /api/admin/**}    - ADMIN only,</li>
 *         <li>{@code /api/student/**}  - STUDENT only (own profile),</li>
 *         <li>everything else        - any authenticated user.</li>
 *       </ul>
 *   </li>
 *   <li>Return clean JSON for 401 (entry point) and 403 (access-denied handler).</li>
 * </ul>
 */
@Configuration
@EnableMethodSecurity // enables @PreAuthorize("hasRole('ADMIN')") on methods when needed
public class SecurityConfig {

    /** Endpoints that must work WITHOUT a token. */
    private static final String[] PUBLIC_ENDPOINTS = {
            "/api/health",
            "/api/health/**",
            "/api/auth/register",   // public self-registration (creates STUDENT)
            "/api/auth/login",      // email + password -> JWT
            "/error"
    };

    private final List<String> allowedOrigins;

    public SecurityConfig(@Value("${app.cors.allowed-origins}") List<String> allowedOrigins) {
        this.allowedOrigins = allowedOrigins;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   JwtAuthenticationFilter jwtAuthenticationFilter,
                                                   RestAuthenticationEntryPoint entryPoint,
                                                   RestAccessDeniedHandler accessDeniedHandler,
                                                   AuthenticationProvider authenticationProvider) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(entryPoint)     // 401 JSON
                        .accessDeniedHandler(accessDeniedHandler)) // 403 JSON
                .authorizeHttpRequests(auth -> auth
                        // Browsers send a pre-flight OPTIONS request before cross-origin calls.
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers(PUBLIC_ENDPOINTS).permitAll()
                        // --- Stage 4: management resources ---
                        // READ: departments & subjects are reference data for any signed-in user;
                        //       student & faculty records are readable only by ADMIN and FACULTY.
                        .requestMatchers(HttpMethod.GET, "/api/departments", "/api/departments/**",
                                                          "/api/subjects", "/api/subjects/**")
                            .authenticated()
                        .requestMatchers(HttpMethod.GET, "/api/students", "/api/students/**",
                                                          "/api/faculty", "/api/faculty/**")
                            .hasAnyRole("ADMIN", "FACULTY")
                        // WRITE: creating/updating/deleting management records is ADMIN-only
                        // (faculty may view but never modify).
                        .requestMatchers("/api/departments", "/api/departments/**",
                                         "/api/subjects", "/api/subjects/**",
                                         "/api/students", "/api/students/**",
                                         "/api/faculty", "/api/faculty/**")
                            .hasRole("ADMIN")
                        // --- Stages 5-11: academic data & insights (ADMIN + FACULTY) ---
                        // Faculty record attendance, marks, assignments and results and view
                        // analytics/predictions/alerts; students use /api/student/** instead.
                        .requestMatchers("/api/attendance", "/api/attendance/**",
                                         "/api/exams", "/api/exams/**",
                                         "/api/marks", "/api/marks/**",
                                         "/api/assignments", "/api/assignments/**",
                                         "/api/submissions", "/api/submissions/**",
                                         "/api/results", "/api/results/**",
                                         "/api/analytics", "/api/analytics/**",
                                         "/api/predictions", "/api/predictions/**",
                                         "/api/alerts", "/api/alerts/**",
                                         "/api/dashboard", "/api/dashboard/**")
                            .hasAnyRole("ADMIN", "FACULTY")
                        // --- Role namespaces ---
                        .requestMatchers("/api/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/student/**").hasRole("STUDENT")
                        .anyRequest().authenticated())
                .authenticationProvider(authenticationProvider)
                // Our filter runs BEFORE the standard username/password filter.
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable);

        return http.build();
    }

    /**
     * Authentication provider that loads users from our database
     * ({@code AppUserDetailsService}) and verifies passwords with BCrypt.
     */
    @Bean
    public AuthenticationProvider authenticationProvider(AppUserDetailsService userDetailsService,
                                                         PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    /** Exposes the configured manager so the login service can authenticate credentials. */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    /** Allows the Vite dev server (http://localhost:5173) to call this API. */
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(allowedOrigins);
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type", "Accept"));
        configuration.setExposedHeaders(List.of("Authorization"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    /** BCrypt is the password hashing algorithm required by the specification. */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}

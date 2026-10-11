package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.security.JwtService;
import com.academic.performance.security.UserPrincipal;

/**
 * End-to-end tests for Stage 3 authentication: registration, login, JWT
 * validation and role-based access.
 *
 * <p>{@code @SpringBootTest} boots the full application (real MySQL, real Spring
 * Security filter chain), {@code @AutoConfigureMockMvc} sends HTTP-like requests
 * through those filters, and {@code @Transactional} rolls every change back so
 * the development database is not polluted. The demo seeder is disabled during
 * tests through the Surefire configuration in {@code pom.xml}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthControllerIntegrationTest {

    private static final AtomicInteger COUNTER = new AtomicInteger();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private JwtService jwtService;

    // ---------------------------------------------------------------- helpers

    private Map<String, String> registerPayload(String username, String email, String password) {
        Map<String, String> body = new HashMap<>();
        body.put("username", username);
        body.put("email", email);
        body.put("password", password);
        return body;
    }

    /** Registers a fresh STUDENT and returns the login email + password. */
    private String registerFreshStudent(String[] credentialsOut) throws Exception {
        int n = COUNTER.incrementAndGet();
        String username = "test_student_" + n;
        String email = "test_student_" + n + "@campus.edu";
        String password = "Secret@123";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerPayload(username, email, password))))
                .andExpect(status().isCreated());

        credentialsOut[0] = email;
        credentialsOut[1] = password;
        return username;
    }

    private String loginAndGetToken(String email, String password) throws Exception {
        MvcResult result = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("email", email, "password", password))))
                .andExpect(status().isOk())
                .andReturn();
        return objectMapper.readTree(result.getResponse().getContentAsString()).get("token").asText();
    }

    /** Creates a user directly and returns a real JWT for them (used for role tests). */
    private String tokenForRole(Role role) {
        int n = COUNTER.incrementAndGet();
        String username = "role_" + role.name().toLowerCase() + "_" + n;
        User user = new User(username, username + "@campus.edu",
                passwordEncoder.encode("Password@123"), role);
        user = userRepository.save(user);
        return jwtService.generateToken(new UserPrincipal(user));
    }

    // ------------------------------------------------------------- register

    @Test
    void registerCreatesStudentAndNeverReturnsPasswordData() throws Exception {
        String username = "reg_" + COUNTER.incrementAndGet();

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerPayload(username, username + "@campus.edu", "Secret@123"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.enabled").value(true))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void registerRejectsDuplicateEmailWith409() throws Exception {
        int n = COUNTER.incrementAndGet();
        String email = "dupe_" + n + "@campus.edu";

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerPayload("dupe_first_" + n, email, "Secret@123"))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerPayload("dupe_second_" + n, email, "Secret@123"))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void registerRejectsInvalidInputWith400AndFieldErrors() throws Exception {
        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                registerPayload("ab", "not-an-email", "short"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.username").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.password").exists());
    }

    // ---------------------------------------------------------------- login

    @Test
    void loginWithValidCredentialsReturnsToken() throws Exception {
        String[] creds = new String[2];
        String username = registerFreshStudent(creds);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", creds[0], "password", creds[1]))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tokenType").value("Bearer"))
                .andExpect(jsonPath("$.expiresIn").isNumber())
                .andExpect(jsonPath("$.user.username").value(username))
                .andExpect(jsonPath("$.user.role").value("STUDENT"))
                .andExpect(jsonPath("$.user.passwordHash").doesNotExist());
    }

    @Test
    void loginWithWrongPasswordIsUnauthorized() throws Exception {
        String[] creds = new String[2];
        registerFreshStudent(creds);

        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", creds[0], "password", "WrongPassword@1"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void loginWithUnknownEmailIsUnauthorized() throws Exception {
        mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(
                                Map.of("email", "nobody@campus.edu", "password", "Whatever@123"))))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    // ------------------------------------------------------- JWT protection

    @Test
    void protectedEndpointWithoutTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void protectedEndpointWithMalformedTokenIsUnauthorized() throws Exception {
        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer not.a.real.token"))
                .andExpect(status().isUnauthorized())
                .andExpect(jsonPath("$.status").value(401));
    }

    @Test
    void meReturnsCurrentUserWhenTokenIsValid() throws Exception {
        String[] creds = new String[2];
        String username = registerFreshStudent(creds);
        String token = loginAndGetToken(creds[0], creds[1]);

        mockMvc.perform(get("/api/auth/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("STUDENT"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    // ------------------------------------------------------ role authority

    @Test
    void studentTokenCannotAccessAdminEndpoint() throws Exception {
        String studentToken = tokenForRole(Role.STUDENT);

        mockMvc.perform(get("/api/admin/probe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.status").value(403));
    }

    @Test
    void adminTokenPassesAuthorizationOnAdminEndpoint() throws Exception {
        String adminToken = tokenForRole(Role.ADMIN);

        // Authorization succeeds, so the request reaches the dispatcher. There is
        // no controller for /api/admin/probe yet, hence 404 - NOT 401 or 403.
        mockMvc.perform(get("/api/admin/probe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken))
                .andExpect(status().isNotFound());
    }

    @Test
    void facultyTokenMayAccessFacultyEndpointButStudentMayNot() throws Exception {
        String facultyToken = tokenForRole(Role.FACULTY);
        mockMvc.perform(get("/api/faculty/probe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + facultyToken))
                .andExpect(status().isNotFound()); // authorized -> reaches dispatcher

        String studentToken = tokenForRole(Role.STUDENT);
        mockMvc.perform(get("/api/faculty/probe")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken))
                .andExpect(status().isForbidden());
    }

    @Test
    void passwordIsStoredAsBCryptHashNotPlainText() throws Exception {
        String[] creds = new String[2];
        registerFreshStudent(creds);

        User stored = userRepository.findByEmail(creds[0]).orElseThrow();
        assertHashedDifferentFromPlainText(stored.getPasswordHash(), creds[1]);
    }

    private void assertHashedDifferentFromPlainText(String hash, String plainText) {
        org.assertj.core.api.Assertions.assertThat(hash).isNotEqualTo(plainText);
        org.assertj.core.api.Assertions.assertThat(hash).startsWith("$2");
        org.assertj.core.api.Assertions.assertThat(passwordEncoder.matches(plainText, hash)).isTrue();
    }
}

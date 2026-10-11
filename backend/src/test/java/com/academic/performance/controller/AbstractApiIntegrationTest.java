package com.academic.performance.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.academic.performance.entity.Department;
import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.repository.FacultyRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.security.JwtService;
import com.academic.performance.security.UserPrincipal;

/**
 * Shared scaffolding for the Stage 4 CRUD integration tests.
 *
 * <p>Boots the whole application against the real MySQL database and sends
 * requests through the genuine Spring Security filter chain. {@code @Transactional}
 * rolls back everything the test wrote, and the demo data-seeder is disabled by
 * the Surefire config in {@code pom.xml} - so tests never pollute or depend on
 * seeded rows (they generate their own uniquely-coded records instead).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
abstract class AbstractApiIntegrationTest {

    /** Simple sequence so every test row gets a unique code/email/roll number. */
    protected static final AtomicInteger SEQ = new AtomicInteger();

    @Autowired
    protected MockMvc mockMvc;
    @Autowired
    protected ObjectMapper objectMapper;
    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected PasswordEncoder passwordEncoder;
    @Autowired
    protected JwtService jwtService;
    @Autowired
    protected DepartmentRepository departmentRepository;
    @Autowired
    protected StudentRepository studentRepository;
    @Autowired
    protected FacultyRepository facultyRepository;
    @Autowired
    protected SubjectRepository subjectRepository;

    // ------------------------------------------------------------- identities

    /** Persists a login account and returns it (password is always "Password@123"). */
    protected User persistUser(Role role) {
        String username = "u_" + role.name().toLowerCase() + "_" + SEQ.incrementAndGet();
        User user = new User(username, username + "@test.edu",
                passwordEncoder.encode("Password@123"), role);
        return userRepository.save(user);
    }

    /** Issues a real JWT for an existing user. */
    protected String tokenFor(User user) {
        return jwtService.generateToken(new UserPrincipal(user));
    }

    protected String tokenForRole(Role role) {
        return tokenFor(persistUser(role));
    }

    protected String adminToken() {
        return tokenForRole(Role.ADMIN);
    }

    protected String facultyToken() {
        return tokenForRole(Role.FACULTY);
    }

    protected String studentToken() {
        return tokenForRole(Role.STUDENT);
    }

    // ----------------------------------------------------------------- data

    /** Persists a uniquely-coded department and returns it. */
    protected Department persistDepartment() {
        return departmentRepository.save(new Department("Test Dept " + SEQ.incrementAndGet(),
                "D" + SEQ.get()));
    }

    protected String json(Object value) throws Exception {
        return objectMapper.writeValueAsString(value);
    }

    protected Map<String, Object> departmentBody(String code) {
        Map<String, Object> body = new HashMap<>();
        body.put("name", "Department " + code);
        body.put("code", code);
        body.put("description", "created by test");
        return body;
    }

    protected Map<String, Object> subjectBody(String code, Long departmentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("code", code);
        body.put("name", "Subject " + code);
        body.put("semester", 3);
        body.put("credits", 4);
        body.put("departmentId", departmentId);
        return body;
    }

    protected Map<String, Object> studentBody(String roll, Long departmentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("rollNumber", roll);
        body.put("registrationNumber", "REG-" + roll);
        body.put("firstName", "Test");
        body.put("lastName", "Student");
        body.put("email", roll.toLowerCase() + "@test.edu");
        body.put("currentSemester", 3);
        body.put("departmentId", departmentId);
        return body;
    }

    protected Map<String, Object> facultyBody(String code, Long departmentId) {
        Map<String, Object> body = new HashMap<>();
        body.put("employeeCode", code);
        body.put("firstName", "Test");
        body.put("lastName", "Faculty");
        body.put("email", code.toLowerCase() + "@test.edu");
        body.put("designation", "Assistant Professor");
        body.put("departmentId", departmentId);
        return body;
    }
}

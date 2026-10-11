package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;

import com.academic.performance.entity.Department;
import com.academic.performance.entity.Role;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.User;

/** Tests for the student self-service endpoint {@code GET /api/student/me}. */
class StudentSelfControllerIntegrationTest extends AbstractApiIntegrationTest {

    /** Persists a student record linked to a fresh STUDENT login and returns the token. */
    private String persistStudentWithLogin(String[] rollOut) {
        User user = persistUser(Role.STUDENT);
        Department department = persistDepartment();
        String roll = "R" + SEQ.incrementAndGet();

        Student student = new Student();
        student.setRollNumber(roll);
        student.setRegistrationNumber("REG-" + roll);
        student.setFirstName("Self");
        student.setLastName("Profile");
        student.setEmail(roll.toLowerCase() + "@test.edu");
        student.setCurrentSemester(4);
        student.setDepartment(department);
        student.setUser(user);
        studentRepository.save(student);

        rollOut[0] = roll;
        return tokenFor(user);
    }

    @Test
    void studentSeesOwnProfile() throws Exception {
        String[] roll = new String[1];
        String token = persistStudentWithLogin(roll);

        mockMvc.perform(get("/api/student/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rollNumber").value(roll[0]))
                .andExpect(jsonPath("$.fullName").value("Self Profile"))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void adminCannotUseStudentSelfEndpoint() throws Exception {
        mockMvc.perform(get("/api/student/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentWithNoLinkedProfileGets404() throws Exception {
        String token = studentToken();

        mockMvc.perform(get("/api/student/me")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }
}

package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.academic.performance.entity.Department;

/** CRUD, validation, duplicate and role tests for {@code /api/students}. */
class StudentApiIntegrationTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanCreateStudentAndNeverSeesSensitiveFields() throws Exception {
        Department department = persistDepartment();
        String roll = "R" + SEQ.incrementAndGet();

        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(studentBody(roll, department.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.rollNumber").value(roll))
                .andExpect(jsonPath("$.fullName").value("Test Student"))
                .andExpect(jsonPath("$.department.id").value(department.getId()))
                .andExpect(jsonPath("$.password").doesNotExist())
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void duplicateRollNumberIsRejectedWith409() throws Exception {
        Department department = persistDepartment();
        String roll = "R" + SEQ.incrementAndGet();
        String token = adminToken();

        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(studentBody(roll, department.getId()))))
                .andExpect(status().isCreated());

        Map<String, Object> duplicate = studentBody(roll, department.getId());
        duplicate.put("email", "unique_" + SEQ.incrementAndGet() + "@test.edu");

        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void duplicateEmailIsRejectedWith409() throws Exception {
        Department department = persistDepartment();
        String token = adminToken();
        String firstRoll = "R" + SEQ.incrementAndGet();

        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(studentBody(firstRoll, department.getId()))))
                .andExpect(status().isCreated());

        Map<String, Object> second = studentBody("R" + SEQ.incrementAndGet(), department.getId());
        second.put("email", firstRoll.toLowerCase() + "@test.edu");

        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(second)))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.Map.of(
                                "rollNumber", "", "registrationNumber", "",
                                "firstName", "", "lastName", "",
                                "email", "not-an-email"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.rollNumber").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.currentSemester").exists())
                .andExpect(jsonPath("$.fieldErrors.departmentId").exists());
    }

    @Test
    void listRequiresAdminOrFaculty() throws Exception {
        mockMvc.perform(get("/api/students"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + facultyToken()))
                .andExpect(status().isOk());
    }

    @Test
    void studentCannotCreateOrDelete() throws Exception {
        Department department = persistDepartment();
        String token = studentToken();

        mockMvc.perform(post("/api/students")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(studentBody("R" + SEQ.incrementAndGet(), department.getId()))))
                .andExpect(status().isForbidden());

        mockMvc.perform(delete("/api/students/1")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isForbidden());
    }

    @Test
    void missingStudentReturns404() throws Exception {
        mockMvc.perform(get("/api/students/99999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }
}

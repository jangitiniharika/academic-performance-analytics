package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.academic.performance.entity.Department;

/** CRUD, validation, duplicate and role tests for {@code /api/faculty}. */
class FacultyApiIntegrationTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanCreateFacultyWithNestedDepartment() throws Exception {
        Department department = persistDepartment();
        String code = "E" + SEQ.incrementAndGet();

        mockMvc.perform(post("/api/faculty")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(facultyBody(code, department.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.employeeCode").value(code))
                .andExpect(jsonPath("$.fullName").value("Test Faculty"))
                .andExpect(jsonPath("$.department.id").value(department.getId()))
                .andExpect(jsonPath("$.passwordHash").doesNotExist());
    }

    @Test
    void duplicateEmployeeCodeIsRejectedWith409() throws Exception {
        Department department = persistDepartment();
        String code = "E" + SEQ.incrementAndGet();
        String token = adminToken();

        mockMvc.perform(post("/api/faculty")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(facultyBody(code, department.getId()))))
                .andExpect(status().isCreated());

        Map<String, Object> duplicate = facultyBody(code, department.getId());
        duplicate.put("email", "unique_" + SEQ.incrementAndGet() + "@test.edu");

        mockMvc.perform(post("/api/faculty")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(duplicate)))
                .andExpect(status().isConflict());
    }

    @Test
    void invalidBodyReturns400() throws Exception {
        mockMvc.perform(post("/api/faculty")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.Map.of(
                                "employeeCode", "", "firstName", "", "lastName", "",
                                "email", "bad"))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.employeeCode").exists())
                .andExpect(jsonPath("$.fieldErrors.email").exists())
                .andExpect(jsonPath("$.fieldErrors.departmentId").exists());
    }

    @Test
    void readsRequireAdminOrFaculty() throws Exception {
        mockMvc.perform(get("/api/faculty"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/faculty")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken()))
                .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/faculty")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + facultyToken()))
                .andExpect(status().isOk());
    }

    @Test
    void missingFacultyReturns404() throws Exception {
        mockMvc.perform(get("/api/faculty/99999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }
}

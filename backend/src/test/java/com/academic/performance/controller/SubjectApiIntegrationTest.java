package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.academic.performance.entity.Department;

/** CRUD, validation, filtering and role tests for {@code /api/subjects}. */
class SubjectApiIntegrationTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanCreateAndReadSubjectWithNestedDepartment() throws Exception {
        Department department = persistDepartment();
        String code = "SUB" + SEQ.incrementAndGet();

        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(subjectBody(code, department.getId()))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value(code))
                .andExpect(jsonPath("$.semester").value(3))
                .andExpect(jsonPath("$.department.id").value(department.getId()))
                .andExpect(jsonPath("$.department.code").value(department.getCode()));
    }

    @Test
    void duplicateCodeIsRejected() throws Exception {
        Department department = persistDepartment();
        String code = "SUB" + SEQ.incrementAndGet();
        String token = adminToken();

        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(subjectBody(code, department.getId()))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(subjectBody(code, department.getId()))))
                .andExpect(status().isConflict());
    }

    @Test
    void missingRequiredFieldsReturn400() throws Exception {
        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.Map.of("code", "", "name", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.semester").exists())
                .andExpect(jsonPath("$.fieldErrors.credits").exists())
                .andExpect(jsonPath("$.fieldErrors.departmentId").exists());
    }

    @Test
    void unknownDepartmentReturns404() throws Exception {
        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(subjectBody("SUBX" + SEQ.incrementAndGet(), 99999999L))))
                .andExpect(status().isNotFound());
    }

    @Test
    void listFiltersByDepartment() throws Exception {
        Department department = persistDepartment();
        String code = "SUB" + SEQ.incrementAndGet();
        String token = adminToken();

        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(subjectBody(code, department.getId()))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/subjects")
                        .param("departmentId", String.valueOf(department.getId()))
                        .param("search", code)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.content[0].code").value(code));
    }

    @Test
    void readsRequireAuthenticationAndWritesRequireAdmin() throws Exception {
        Department department = persistDepartment();

        mockMvc.perform(get("/api/subjects"))
                .andExpect(status().isUnauthorized());

        // Faculty can read...
        mockMvc.perform(get("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + facultyToken()))
                .andExpect(status().isOk());

        // ...but cannot write.
        mockMvc.perform(post("/api/subjects")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + facultyToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(subjectBody("SUBF" + SEQ.incrementAndGet(), department.getId()))))
                .andExpect(status().isForbidden());
    }
}

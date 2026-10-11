package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/** CRUD, validation, duplicate and role tests for {@code /api/departments}. */
class DepartmentApiIntegrationTest extends AbstractApiIntegrationTest {

    @Test
    void adminCanCreateReadUpdateAndDelete() throws Exception {
        String token = adminToken();
        String code = "DP" + SEQ.incrementAndGet();

        String created = mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody(code))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.code").value(code))
                .andReturn().getResponse().getContentAsString();
        long id = objectMapper.readTree(created).get("id").asLong();

        mockMvc.perform(get("/api/departments/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.code").value(code));

        mockMvc.perform(put("/api/departments/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody(code)).replace("created by test", "renamed")))
                .andExpect(status().isOk());

        mockMvc.perform(delete("/api/departments/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNoContent());

        mockMvc.perform(get("/api/departments/" + id)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isNotFound());
    }

    @Test
    void duplicateCodeIsRejectedWith409() throws Exception {
        String token = adminToken();
        String code = "DP" + SEQ.incrementAndGet();

        mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody(code))))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody(code))))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void invalidBodyReturns400WithFieldErrors() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(java.util.Map.of("name", "", "code", ""))))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.fieldErrors.name").exists())
                .andExpect(jsonPath("$.fieldErrors.code").exists());
    }

    @Test
    void listSupportsSearchAndPagination() throws Exception {
        String token = adminToken();
        String code = "DP" + SEQ.incrementAndGet();

        mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody(code))))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/departments")
                        .param("search", code)
                        .param("page", "0")
                        .param("size", "5")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content[0].code").value(code))
                .andExpect(jsonPath("$.totalElements").value(1))
                .andExpect(jsonPath("$.size").value(5));
    }

    @Test
    void missingRecordReturns404() throws Exception {
        mockMvc.perform(get("/api/departments/99999999")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + adminToken()))
                .andExpect(status().isNotFound());
    }

    @Test
    void readsRequireAuthentication() throws Exception {
        mockMvc.perform(get("/api/departments"))
                .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken()))
                .andExpect(status().isOk());
    }

    @Test
    void facultyMayReadButNotWrite() throws Exception {
        String token = facultyToken();

        mockMvc.perform(get("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token))
                .andExpect(status().isOk());

        mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody("DPX" + SEQ.incrementAndGet()))))
                .andExpect(status().isForbidden());
    }

    @Test
    void studentMayNotWrite() throws Exception {
        mockMvc.perform(post("/api/departments")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(departmentBody("DPS" + SEQ.incrementAndGet()))))
                .andExpect(status().isForbidden());
    }
}

package com.academic.performance.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import com.academic.performance.entity.Department;
import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;

/**
 * End-to-end integration tests for Stages 5-11: attendance, exams, marks,
 * assignments, results, analytics, prediction, alerts and the dashboards.
 *
 * <p>Exercises the real Spring Security chain and the real MySQL database; the
 * {@code @Transactional} base class rolls everything back afterwards.
 */
class AcademicWorkflowIntegrationTest extends AbstractApiIntegrationTest {

    private static final String AUTH = HttpHeaders.AUTHORIZATION;

    // ------------------------------------------------------------- helpers

    private String bearer(String token) {
        return "Bearer " + token;
    }

    private long createAndGetId(String path, String token, Object body) throws Exception {
        String response = mockMvc.perform(post(path)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(body)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }

    private Map<String, Object> attendanceBody(Long studentId, Long subjectId, String status, LocalDate date) {
        Map<String, Object> body = new HashMap<>();
        body.put("studentId", studentId);
        body.put("subjectId", subjectId);
        body.put("classDate", date.toString());
        body.put("status", status);
        return body;
    }

    private String bearer(String token, boolean unused) {
        return token;
    }

    @Test
    void fullAcademicWorkflowProducesAnalyticsPredictionAndDashboard() throws Exception {
        String admin = adminToken();
        Department department = persistDepartment();

        long subjectId = createAndGetId("/api/subjects", admin,
                subjectBody("WF" + SEQ.incrementAndGet(), department.getId()));

        User studentUser = persistUser(Role.STUDENT);
        String roll = "R" + SEQ.incrementAndGet();
        Map<String, Object> student = studentBody(roll, department.getId());
        student.put("userId", studentUser.getId());
        long studentId = createAndGetId("/api/students", admin, student);
        String studentToken = tokenFor(studentUser);

        Map<String, Object> exam = new HashMap<>();
        exam.put("name", "Mid-Term");
        exam.put("subjectId", subjectId);
        exam.put("examDate", LocalDate.now().minusDays(5).toString());
        exam.put("maxMarks", 100);
        long examId = createAndGetId("/api/exams", admin, exam);

        Map<String, Object> marks = new HashMap<>();
        marks.put("studentId", studentId);
        marks.put("examId", examId);
        marks.put("marksObtained", 60);
        mockMvc.perform(post("/api/marks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(marks)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.percentage").value(60.00));

        Map<String, Object> attendance = new HashMap<>();
        attendance.put("studentId", studentId);
        attendance.put("subjectId", subjectId);
        attendance.put("classDate", LocalDate.now().minusDays(1).toString());
        attendance.put("status", "PRESENT");
        mockMvc.perform(post("/api/attendance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(attendance)))
                .andExpect(status().isCreated());

        Map<String, Object> assignment = new HashMap<>();
        assignment.put("title", "Unit 1 assignment");
        assignment.put("subjectId", subjectId);
        assignment.put("dueDate", LocalDate.now().plusDays(7).toString());
        assignment.put("maxMarks", 50);
        long assignmentId = createAndGetId("/api/assignments", admin, assignment);

        Map<String, Object> submission = new HashMap<>();
        submission.put("assignmentId", assignmentId);
        submission.put("studentId", studentId);
        submission.put("status", "GRADED");
        submission.put("marks", 40);
        mockMvc.perform(post("/api/submissions")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(submission)))
                .andExpect(status().isCreated());

        Map<String, Object> result = new HashMap<>();
        result.put("studentId", studentId);
        result.put("subjectId", subjectId);
        result.put("semester", 3);
        result.put("grade", "A");
        result.put("gradePoints", 8.0);
        result.put("credits", 4);
        mockMvc.perform(post("/api/results")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(result)))
                .andExpect(status().isCreated());

        // --- analytics --------------------------------------------------------
        mockMvc.perform(get("/api/analytics/student/" + studentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.rollNumber").value(roll))
                .andExpect(jsonPath("$.attendancePercentage").value(100.00))
                .andExpect(jsonPath("$.averageMarksPercentage").value(60.00))
                .andExpect(jsonPath("$.assignmentPercentage").value(80.00))
                .andExpect(jsonPath("$.cgpa").value(8.0))
                .andExpect(jsonPath("$.subjects").isArray());

        // --- prediction (40/30/15/15 weights) ---------------------------------
        // 0.40*80 + 0.30*60 + 0.15*100 + 0.15*80 = 77.00 -> LOW risk, grade B
        mockMvc.perform(post("/api/predictions/generate/" + studentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictedScore").value(77.00))
                .andExpect(jsonPath("$.predictedGrade").value("B"))
                .andExpect(jsonPath("$.riskLevel").value("LOW"))
                .andExpect(jsonPath("$.missingIndicators").isEmpty());

        // --- alerts: everything is healthy, so nothing is raised --------------
        mockMvc.perform(post("/api/alerts/generate/" + studentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(0));

        // --- admin dashboard --------------------------------------------------
        mockMvc.perform(get("/api/dashboard/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.overview.totalStudents").isNumber())
                .andExpect(jsonPath("$.atRiskStudents").isArray());

        // --- student sees only their own data --------------------------------
        mockMvc.perform(get("/api/student/dashboard")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.analytics.rollNumber").value(roll));

        mockMvc.perform(get("/api/student/attendance/percentage")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.percentage").value(100.00));

        mockMvc.perform(get("/api/student/cgpa")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.cgpa").value(8.0));

        mockMvc.perform(get("/api/student/prediction")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + studentToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictedScore").value(77.00));
    }

    @Test
    void lowAttendanceStudentIsFlaggedByAlertsAndPrediction() throws Exception {
        String admin = adminToken();
        Department department = persistDepartment();
        long subjectId = createAndGetId("/api/subjects", admin,
                subjectBody("AL" + SEQ.incrementAndGet(), department.getId()));

        String roll = "R" + SEQ.incrementAndGet();
        long studentId = createAndGetId("/api/students", admin, studentBody(roll, department.getId()));

        Map<String, Object> attendance = new HashMap<>();
        attendance.put("studentId", studentId);
        attendance.put("subjectId", subjectId);
        attendance.put("classDate", LocalDate.now().minusDays(2).toString());
        attendance.put("status", "ABSENT");
        mockMvc.perform(post("/api/attendance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(attendance)))
                .andExpect(status().isCreated());

        // Only attendance data exists: score 0 -> HIGH risk.
        mockMvc.perform(post("/api/predictions/generate/" + studentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.predictedScore").value(0.00))
                .andExpect(jsonPath("$.riskLevel").value("HIGH"))
                .andExpect(jsonPath("$.missingIndicators").isNotEmpty());

        // Two alerts expected: ATTENDANCE and PREDICTION.
        mockMvc.perform(post("/api/alerts/generate/" + studentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2));

        mockMvc.perform(get("/api/alerts")
                        .param("studentId", String.valueOf(studentId))
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.totalElements").value(2));
    }

    @Test
    void duplicateAttendanceAndMarksAreRejected() throws Exception {
        String admin = adminToken();
        Department department = persistDepartment();
        long subjectId = createAndGetId("/api/subjects", admin,
                subjectBody("DUP" + SEQ.incrementAndGet(), department.getId()));
        long studentId = createAndGetId("/api/students", admin,
                studentBody("R" + SEQ.incrementAndGet(), department.getId()));

        Map<String, Object> exam = new HashMap<>();
        exam.put("name", "Quiz");
        exam.put("subjectId", subjectId);
        exam.put("examDate", LocalDate.now().minusDays(1).toString());
        exam.put("maxMarks", 20);
        long examId = createAndGetId("/api/exams", admin, exam);

        Map<String, Object> marks = new HashMap<>();
        marks.put("studentId", studentId);
        marks.put("examId", examId);
        marks.put("marksObtained", 15);
        mockMvc.perform(post("/api/marks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(marks)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/marks")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(marks)))
                .andExpect(status().isConflict());

        Map<String, Object> attendance = new HashMap<>();
        attendance.put("studentId", studentId);
        attendance.put("subjectId", subjectId);
        attendance.put("classDate", LocalDate.now().minusDays(3).toString());
        attendance.put("status", "PRESENT");
        mockMvc.perform(post("/api/attendance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(attendance)))
                .andExpect(status().isCreated());
        mockMvc.perform(post("/api/attendance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(attendance)))
                .andExpect(status().isConflict());
    }

    @Test
    void academicInsightEndpointsAreRoleGuarded() throws Exception {
        // No token -> 401.
        mockMvc.perform(get("/api/analytics/overview")).andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/dashboard/admin")).andExpect(status().isUnauthorized());

        // A student may not read institution analytics or the admin dashboard.
        String student = studentToken();
        mockMvc.perform(get("/api/analytics/overview")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + student))
                .andExpect(status().isForbidden());
        mockMvc.perform(get("/api/dashboard/admin")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + student))
                .andExpect(status().isForbidden());

        // Faculty may read analytics.
        mockMvc.perform(get("/api/analytics/overview")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + facultyToken()))
                .andExpect(status().isOk());
    }

    @Test
    void acknowledgingAnAlertMarksItResolved() throws Exception {
        String admin = adminToken();
        Department department = persistDepartment();
        long subjectId = createAndGetId("/api/subjects", admin,
                subjectBody("ACK" + SEQ.incrementAndGet(), department.getId()));
        long studentId = createAndGetId("/api/students", admin,
                studentBody("R" + SEQ.incrementAndGet(), department.getId()));

        Map<String, Object> attendance = new HashMap<>();
        attendance.put("studentId", studentId);
        attendance.put("subjectId", subjectId);
        attendance.put("classDate", LocalDate.now().minusDays(1).toString());
        attendance.put("status", "ABSENT");
        mockMvc.perform(post("/api/attendance")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(json(attendance)))
                .andExpect(status().isCreated());

        String created = mockMvc.perform(post("/api/alerts/generate/" + studentId)
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andReturn().getResponse().getContentAsString();
        long alertId = objectMapper.readTree(created).get(0).get("id").asLong();

        mockMvc.perform(patch("/api/alerts/" + alertId + "/acknowledge")
                        .header(HttpHeaders.AUTHORIZATION, "Bearer " + admin))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.acknowledged").value(true))
                .andExpect(jsonPath("$.resolvedAt").exists());
    }
}

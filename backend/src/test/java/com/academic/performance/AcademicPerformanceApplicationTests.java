package com.academic.performance;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Smoke test: the full Spring application context (web layer, JPA, security,
 * datasource) starts without errors.
 *
 * <p>This test needs the project-local MySQL server to be running:
 * {@code database\start-mysql.cmd}
 */
@SpringBootTest
class AcademicPerformanceApplicationTests {

    @Test
    void contextLoads() {
        // If the context fails to start, this test fails and shows why.
    }
}

package com.academic.performance.controller;

import java.sql.Connection;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Public health endpoint used to verify that the backend is running and that it
 * can reach the MySQL database.
 *
 * <p>GET /api/health returns HTTP 200 with {@code status = "UP"} when the whole
 * stack (web layer + MySQL connection) is healthy, or HTTP 503 otherwise. The
 * React landing page calls this endpoint to prove the frontend/backend wiring
 * works - no hard-coded "connected" state anywhere.
 */
@RestController
@RequestMapping("/api/health")
public class HealthController {

    private final DataSource dataSource;
    private final String applicationName;

    public HealthController(DataSource dataSource,
                            @Value("${spring.application.name:academic-performance-platform}") String applicationName) {
        this.dataSource = dataSource;
        this.applicationName = applicationName;
    }

    @GetMapping
    public ResponseEntity<Map<String, Object>> health() {
        boolean databaseUp;
        String databaseProduct = "unknown";

        try (Connection connection = dataSource.getConnection()) {
            databaseUp = connection.isValid(2);
            if (databaseUp) {
                var metaData = connection.getMetaData();
                databaseProduct = metaData.getDatabaseProductName() + " " + metaData.getDatabaseProductVersion();
            }
        } catch (Exception ex) {
            // The web layer is up but the database is unreachable.
            databaseUp = false;
        }

        Map<String, Object> database = new LinkedHashMap<>();
        database.put("status", databaseUp ? "UP" : "DOWN");
        database.put("product", databaseProduct);

        Map<String, Object> body = new LinkedHashMap<>();
        body.put("status", databaseUp ? "UP" : "DOWN");
        body.put("application", applicationName);
        body.put("timestamp", Instant.now().toString());
        body.put("database", database);

        HttpStatus status = databaseUp ? HttpStatus.OK : HttpStatus.SERVICE_UNAVAILABLE;
        return ResponseEntity.status(status).body(body);
    }
}

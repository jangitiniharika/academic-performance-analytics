package com.academic.performance.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.PredictionResponse;
import com.academic.performance.service.PredictionService;

/**
 * Prediction endpoints (Stage 10). Restricted to ADMIN + FACULTY.
 *
 * <p>{@code POST /generate/{id}} runs the weighted estimate and stores a snapshot;
 * {@code GET /student/{id}} returns the most recent snapshot.
 */
@RestController
@RequestMapping("/api/predictions")
public class PredictionController {

    private final PredictionService predictionService;

    public PredictionController(PredictionService predictionService) {
        this.predictionService = predictionService;
    }

    @PostMapping("/generate/{studentId:\\d+}")
    public PredictionResponse generate(@PathVariable Long studentId) {
        return predictionService.generate(studentId);
    }

    @GetMapping("/student/{studentId:\\d+}")
    public ResponseEntity<PredictionResponse> latest(@PathVariable Long studentId) {
        PredictionResponse latest = predictionService.latest(studentId);
        return latest == null ? ResponseEntity.noContent().build() : ResponseEntity.ok(latest);
    }
}

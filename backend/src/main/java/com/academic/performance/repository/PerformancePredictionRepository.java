package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.PerformancePrediction;
import com.academic.performance.entity.RiskLevel;

/** Database access for {@link PerformancePrediction}. */
@Repository
public interface PerformancePredictionRepository extends JpaRepository<PerformancePrediction, Long> {

    /** The most recent prediction for a student (used by the dashboards). */
    Optional<PerformancePrediction> findTopByStudentIdOrderByGeneratedAtDesc(Long studentId);

    /** Full prediction history for a student, newest first. */
    List<PerformancePrediction> findByStudentIdOrderByGeneratedAtDesc(Long studentId);

    List<PerformancePrediction> findByRiskLevel(RiskLevel riskLevel);

    long countByRiskLevel(RiskLevel riskLevel);
}

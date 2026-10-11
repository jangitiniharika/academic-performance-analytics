package com.academic.performance.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;

import org.junit.jupiter.api.Test;

/**
 * Unit tests for {@link AcademicCalculator}. No Spring context is needed -
 * these are pure calculations, so they run in milliseconds.
 */
class AcademicCalculatorTest {

    @Test
    void marksPercentageIsComputedCorrectly() {
        // 30 out of 40 = 75.00%
        assertEquals(new BigDecimal("75.00"),
                AcademicCalculator.marksPercentage(new BigDecimal("30"), 40).orElseThrow());
    }

    @Test
    void marksPercentageIsEmptyWhenDataIsMissing() {
        // Missing data is NOT treated as zero - the result is empty.
        assertTrue(AcademicCalculator.marksPercentage(null, 40).isEmpty());
        assertTrue(AcademicCalculator.marksPercentage(new BigDecimal("30"), null).isEmpty());
    }

    @Test
    void marksPercentageGuardsAgainstDivisionByZero() {
        assertTrue(AcademicCalculator.marksPercentage(new BigDecimal("30"), 0).isEmpty());
    }

    @Test
    void marksPercentageIsClampedTo100() {
        // 50 out of 40 would be 125% - clamped to 100.00
        assertEquals(new BigDecimal("100.00"),
                AcademicCalculator.marksPercentage(new BigDecimal("50"), 40).orElseThrow());
    }

    @Test
    void attendancePercentageIsComputedCorrectly() {
        // 18 out of 20 sessions = 90.00%
        assertEquals(new BigDecimal("90.00"),
                AcademicCalculator.attendancePercentage(18, 20).orElseThrow());
    }

    @Test
    void attendancePercentageIsEmptyWhenThereAreNoSessions() {
        assertTrue(AcademicCalculator.attendancePercentage(0, 0).isEmpty());
    }
}

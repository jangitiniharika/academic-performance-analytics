package com.academic.performance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Optional;

/**
 * Pure helper that turns raw counts/marks into safe percentages.
 *
 * <p>It contains no database or Spring code, so it is trivial to unit test and
 * to explain. Every method:
 * <ul>
 *   <li>never throws on missing data - it returns {@link Optional#empty()}
 *       instead of guessing a value (missing data is NOT treated as zero),</li>
 *   <li>guards against division by zero,</li>
 *   <li>clamps the result to the valid 0-100 range.</li>
 * </ul>
 */
public final class AcademicCalculator {

    private static final BigDecimal HUNDRED = BigDecimal.valueOf(100);
    private static final int SCALE = 2;

    private AcademicCalculator() {
        // utility class - never instantiated
    }

    /**
     * Marks percentage = (marks obtained / maximum marks) x 100.
     *
     * @return empty when marks or the maximum are missing / invalid; otherwise a
     *         0-100 value rounded to 2 decimals.
     */
    public static Optional<BigDecimal> marksPercentage(BigDecimal marksObtained, Integer maxMarks) {
        if (marksObtained == null || maxMarks == null || maxMarks <= 0) {
            return Optional.empty();
        }
        return Optional.of(clamp(
                marksObtained.multiply(HUNDRED)
                        .divide(BigDecimal.valueOf(maxMarks), SCALE, RoundingMode.HALF_UP)));
    }

    /**
     * Attendance percentage = (attended / total sessions) x 100.
     *
     * @return empty when there are no sessions yet; otherwise a 0-100 value.
     */
    public static Optional<BigDecimal> attendancePercentage(long attendedSessions, long totalSessions) {
        if (totalSessions <= 0) {
            return Optional.empty();
        }
        long safeAttended = Math.max(0, Math.min(attendedSessions, totalSessions));
        return Optional.of(
                BigDecimal.valueOf(safeAttended)
                        .multiply(HUNDRED)
                        .divide(BigDecimal.valueOf(totalSessions), SCALE, RoundingMode.HALF_UP));
    }

    /** Keeps a value inside the 0-100 range. */
    private static BigDecimal clamp(BigDecimal value) {
        if (value.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO.setScale(SCALE, RoundingMode.HALF_UP);
        }
        if (value.compareTo(HUNDRED) > 0) {
            return HUNDRED.setScale(SCALE, RoundingMode.HALF_UP);
        }
        return value;
    }
}

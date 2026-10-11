package com.academic.performance.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/** Small numeric helpers that keep analytics code free of repeated null/scale handling. */
public final class Numbers {

    private Numbers() {
    }

    /**
     * Averages the non-null values, rounded to 2 decimals.
     *
     * @return {@code null} when there is nothing to average (data is missing, not zero).
     */
    public static BigDecimal average(Collection<BigDecimal> values) {
        if (values == null) {
            return null;
        }
        List<BigDecimal> present = values.stream().filter(Objects::nonNull).toList();
        if (present.isEmpty()) {
            return null;
        }
        BigDecimal sum = present.stream().reduce(BigDecimal.ZERO, BigDecimal::add);
        return sum.divide(BigDecimal.valueOf(present.size()), 2, RoundingMode.HALF_UP);
    }

    /** Sets a 2-decimal scale, or returns {@code null} unchanged. */
    public static BigDecimal round(BigDecimal value) {
        return value == null ? null : value.setScale(2, RoundingMode.HALF_UP);
    }
}

package com.academic.performance.util;

/**
 * Tiny helpers for cleaning up user-supplied text.
 *
 * <p>Blank strings from an HTTP query/form are converted to {@code null} so that
 * optional filters and columns behave predictably (an empty search means "no
 * filter", not "search for an empty string").
 */
public final class TextUtils {

    private TextUtils() {
        // utility class - not instantiable
    }

    /** Trims the value; returns {@code null} for {@code null} or blank input. */
    public static String normalizeSearch(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    /** Trims the value; returns {@code null} for {@code null} or blank input. */
    public static String trimToNull(String value) {
        return normalizeSearch(value);
    }
}

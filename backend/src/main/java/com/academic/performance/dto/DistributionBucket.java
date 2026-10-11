package com.academic.performance.dto;

/** A single bucket of a score distribution chart, e.g. {@code "70-85" -> 4}. */
public record DistributionBucket(String label, long count) {
}

package com.academic.performance.entity;

/** Lifecycle of a student's submission for an assignment. */
public enum SubmissionStatus {
    PENDING,   // not yet submitted
    SUBMITTED, // submitted, not yet graded
    GRADED,    // marks awarded
    LATE       // submitted after the due date
}

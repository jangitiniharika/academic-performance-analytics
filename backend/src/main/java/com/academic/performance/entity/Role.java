package com.academic.performance.entity;

/**
 * The three user roles of the application.
 *
 * <p>Used for role-based authorization (Stage 3+): an ADMIN manages the whole
 * institution, a FACULTY member manages their classes, and a STUDENT can only
 * see their own records.
 */
public enum Role {
    ADMIN,
    FACULTY,
    STUDENT
}

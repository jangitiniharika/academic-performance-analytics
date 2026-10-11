package com.academic.performance.dto;

import com.academic.performance.entity.Subject;

/** Compact reference to a subject, embedded inside other responses. */
public record SubjectSummary(Long id, String code, String name) {

    public static SubjectSummary of(Subject subject) {
        return subject == null
                ? null
                : new SubjectSummary(subject.getId(), subject.getCode(), subject.getName());
    }
}

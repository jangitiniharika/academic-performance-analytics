package com.academic.performance.dto;

import com.academic.performance.entity.Student;

/** Compact reference to a student, embedded inside other responses. */
public record StudentSummary(Long id, String rollNumber, String fullName) {

    public static StudentSummary of(Student student) {
        return student == null
                ? null
                : new StudentSummary(student.getId(), student.getRollNumber(), student.getFullName());
    }
}

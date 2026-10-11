package com.academic.performance.mapper;

import org.springframework.stereotype.Component;

import com.academic.performance.dto.SubjectResponse;
import com.academic.performance.entity.Subject;

/** Converts {@link Subject} entities to {@link SubjectResponse} DTOs. */
@Component
public class SubjectMapper {

    private final DepartmentMapper departmentMapper;

    public SubjectMapper(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    public SubjectResponse toResponse(Subject subject) {
        if (subject == null) {
            return null;
        }
        Long facultyId = subject.getFaculty() != null ? subject.getFaculty().getId() : null;
        return new SubjectResponse(
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                subject.getSemester(),
                subject.getCredits(),
                departmentMapper.toSummary(subject.getDepartment()),
                facultyId,
                subject.getCreatedAt(),
                subject.getUpdatedAt());
    }
}

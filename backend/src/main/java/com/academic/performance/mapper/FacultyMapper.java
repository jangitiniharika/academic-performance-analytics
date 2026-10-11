package com.academic.performance.mapper;

import org.springframework.stereotype.Component;

import com.academic.performance.dto.FacultyResponse;
import com.academic.performance.entity.Faculty;

/** Converts {@link Faculty} entities to {@link FacultyResponse} DTOs. */
@Component
public class FacultyMapper {

    private final DepartmentMapper departmentMapper;

    public FacultyMapper(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    public FacultyResponse toResponse(Faculty faculty) {
        if (faculty == null) {
            return null;
        }
        Long userId = faculty.getUser() != null ? faculty.getUser().getId() : null;
        return new FacultyResponse(
                faculty.getId(),
                faculty.getEmployeeCode(),
                faculty.getFirstName(),
                faculty.getLastName(),
                faculty.getFullName(),
                faculty.getEmail(),
                faculty.getPhone(),
                faculty.getDesignation(),
                departmentMapper.toSummary(faculty.getDepartment()),
                userId,
                faculty.getCreatedAt(),
                faculty.getUpdatedAt());
    }
}

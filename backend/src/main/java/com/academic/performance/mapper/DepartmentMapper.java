package com.academic.performance.mapper;

import org.springframework.stereotype.Component;

import com.academic.performance.dto.DepartmentResponse;
import com.academic.performance.dto.DepartmentSummary;
import com.academic.performance.entity.Department;

/**
 * Converts {@link Department} entities to their DTOs.
 *
 * <p>{@link #toSummary(Department)} is reused by the Subject/Student/Faculty
 * mappers so the nested department JSON always has the same shape.
 */
@Component
public class DepartmentMapper {

    public DepartmentResponse toResponse(Department department) {
        if (department == null) {
            return null;
        }
        return new DepartmentResponse(
                department.getId(),
                department.getName(),
                department.getCode(),
                department.getDescription(),
                department.getCreatedAt(),
                department.getUpdatedAt());
    }

    public DepartmentSummary toSummary(Department department) {
        if (department == null) {
            return null;
        }
        return new DepartmentSummary(department.getId(), department.getName(), department.getCode());
    }
}

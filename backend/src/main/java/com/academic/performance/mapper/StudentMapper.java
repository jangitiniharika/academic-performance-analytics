package com.academic.performance.mapper;

import org.springframework.stereotype.Component;

import com.academic.performance.dto.StudentResponse;
import com.academic.performance.entity.Student;

/** Converts {@link Student} entities to {@link StudentResponse} DTOs. */
@Component
public class StudentMapper {

    private final DepartmentMapper departmentMapper;

    public StudentMapper(DepartmentMapper departmentMapper) {
        this.departmentMapper = departmentMapper;
    }

    public StudentResponse toResponse(Student student) {
        if (student == null) {
            return null;
        }
        Long userId = student.getUser() != null ? student.getUser().getId() : null;
        return new StudentResponse(
                student.getId(),
                student.getRollNumber(),
                student.getRegistrationNumber(),
                student.getFirstName(),
                student.getLastName(),
                student.getFullName(),
                student.getEmail(),
                student.getPhone(),
                student.getCurrentSemester(),
                student.getCgpa(),
                student.getAdmissionYear(),
                departmentMapper.toSummary(student.getDepartment()),
                userId,
                student.getCreatedAt(),
                student.getUpdatedAt());
    }
}

package com.academic.performance.controller;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.StudentRequest;
import com.academic.performance.dto.StudentResponse;
import com.academic.performance.service.AccessScopeService;
import com.academic.performance.service.StudentService;

/**
 * REST endpoints for Students (management view: ADMIN + FACULTY).
 *
 * <p>A student's own profile is served separately by {@code /api/student/me}.
 * A FACULTY member only sees the students of their own department; an ADMIN sees
 * every student.
 */
@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final AccessScopeService accessScopeService;

    public StudentController(StudentService studentService, AccessScopeService accessScopeService) {
        this.studentService = studentService;
        this.accessScopeService = accessScopeService;
    }

    @GetMapping
    public PageResponse<StudentResponse> list(@RequestParam(required = false) Long departmentId,
                                              @RequestParam(required = false) String search,
                                              @PageableDefault(size = 20, sort = "rollNumber") Pageable pageable) {
        // A scoped faculty member is pinned to their own department.
        AccessScopeService.AccessScope scope = accessScopeService.currentScope();
        Long scopedDepartmentId = scope.departmentId() != null ? scope.departmentId() : departmentId;
        return studentService.list(scopedDepartmentId, search, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public StudentResponse get(@PathVariable Long id) {
        return studentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        return studentService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        return studentService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        studentService.delete(id);
    }
}

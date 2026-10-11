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

import com.academic.performance.dto.AssignmentRequest;
import com.academic.performance.dto.AssignmentResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.service.AssignmentService;

/** REST endpoints for Assignments (Stage 7). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/assignments")
public class AssignmentController {

    private final AssignmentService assignmentService;

    public AssignmentController(AssignmentService assignmentService) {
        this.assignmentService = assignmentService;
    }

    @GetMapping
    public PageResponse<AssignmentResponse> list(@RequestParam(required = false) Long subjectId,
                                                 @RequestParam(required = false) Long facultyId,
                                                 @PageableDefault(size = 20, sort = "dueDate") Pageable pageable) {
        return assignmentService.list(subjectId, facultyId, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public AssignmentResponse get(@PathVariable Long id) {
        return assignmentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AssignmentResponse create(@Valid @RequestBody AssignmentRequest request) {
        return assignmentService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public AssignmentResponse update(@PathVariable Long id, @Valid @RequestBody AssignmentRequest request) {
        return assignmentService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        assignmentService.delete(id);
    }
}

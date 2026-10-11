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
import com.academic.performance.dto.SubmissionRequest;
import com.academic.performance.dto.SubmissionResponse;
import com.academic.performance.service.SubmissionService;

/** REST endpoints for assignment submissions / grading (Stage 7). ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/submissions")
public class SubmissionController {

    private final SubmissionService submissionService;

    public SubmissionController(SubmissionService submissionService) {
        this.submissionService = submissionService;
    }

    @GetMapping
    public PageResponse<SubmissionResponse> list(@RequestParam(required = false) Long assignmentId,
                                                 @RequestParam(required = false) Long studentId,
                                                 @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return submissionService.list(assignmentId, studentId, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public SubmissionResponse get(@PathVariable Long id) {
        return submissionService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubmissionResponse create(@Valid @RequestBody SubmissionRequest request) {
        return submissionService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public SubmissionResponse update(@PathVariable Long id, @Valid @RequestBody SubmissionRequest request) {
        return submissionService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        submissionService.delete(id);
    }
}

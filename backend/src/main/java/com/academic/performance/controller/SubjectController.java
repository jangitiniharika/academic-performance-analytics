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
import com.academic.performance.dto.SubjectRequest;
import com.academic.performance.dto.SubjectResponse;
import com.academic.performance.service.SubjectService;

/**
 * REST endpoints for Subjects.
 *
 * <p>Supports optional filters: {@code departmentId}, {@code semester} and
 * {@code search} (name or code), plus pagination/sorting.
 */
@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    private final SubjectService subjectService;

    public SubjectController(SubjectService subjectService) {
        this.subjectService = subjectService;
    }

    @GetMapping
    public PageResponse<SubjectResponse> list(@RequestParam(required = false) Long departmentId,
                                              @RequestParam(required = false) Integer semester,
                                              @RequestParam(required = false) String search,
                                              @PageableDefault(size = 20, sort = "code") Pageable pageable) {
        return subjectService.list(departmentId, semester, search, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public SubjectResponse get(@PathVariable Long id) {
        return subjectService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SubjectResponse create(@Valid @RequestBody SubjectRequest request) {
        return subjectService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public SubjectResponse update(@PathVariable Long id, @Valid @RequestBody SubjectRequest request) {
        return subjectService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        subjectService.delete(id);
    }
}

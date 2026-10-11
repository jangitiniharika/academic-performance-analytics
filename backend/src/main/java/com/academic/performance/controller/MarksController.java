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

import com.academic.performance.dto.MarksRequest;
import com.academic.performance.dto.MarksResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.service.MarksService;

/** REST endpoints for Marks (Stage 6). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/marks")
public class MarksController {

    private final MarksService marksService;

    public MarksController(MarksService marksService) {
        this.marksService = marksService;
    }

    @GetMapping
    public PageResponse<MarksResponse> list(@RequestParam(required = false) Long studentId,
                                            @RequestParam(required = false) Long examId,
                                            @RequestParam(required = false) Long subjectId,
                                            @PageableDefault(size = 20, sort = "id") Pageable pageable) {
        return marksService.list(studentId, examId, subjectId, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public MarksResponse get(@PathVariable Long id) {
        return marksService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MarksResponse create(@Valid @RequestBody MarksRequest request) {
        return marksService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public MarksResponse update(@PathVariable Long id, @Valid @RequestBody MarksRequest request) {
        return marksService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        marksService.delete(id);
    }
}

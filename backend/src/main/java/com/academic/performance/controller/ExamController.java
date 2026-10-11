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

import com.academic.performance.dto.ExamRequest;
import com.academic.performance.dto.ExamResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.service.ExamService;

/** REST endpoints for Exams (Stage 6). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/exams")
public class ExamController {

    private final ExamService examService;

    public ExamController(ExamService examService) {
        this.examService = examService;
    }

    @GetMapping
    public PageResponse<ExamResponse> list(@RequestParam(required = false) Long subjectId,
                                           @PageableDefault(size = 20, sort = "examDate") Pageable pageable) {
        return examService.list(subjectId, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public ExamResponse get(@PathVariable Long id) {
        return examService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ExamResponse create(@Valid @RequestBody ExamRequest request) {
        return examService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public ExamResponse update(@PathVariable Long id, @Valid @RequestBody ExamRequest request) {
        return examService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        examService.delete(id);
    }
}

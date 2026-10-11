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

import com.academic.performance.dto.CgpaResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.ResultRequest;
import com.academic.performance.dto.ResultResponse;
import com.academic.performance.service.ResultService;

/** REST endpoints for Academic results and CGPA (Stage 8). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/results")
public class ResultController {

    private final ResultService resultService;

    public ResultController(ResultService resultService) {
        this.resultService = resultService;
    }

    @GetMapping
    public PageResponse<ResultResponse> list(@RequestParam(required = false) Long studentId,
                                             @RequestParam(required = false) Integer semester,
                                             @PageableDefault(size = 20, sort = "semester") Pageable pageable) {
        return resultService.list(studentId, semester, pageable);
    }

    @GetMapping("/cgpa/{studentId:\\d+}")
    public CgpaResponse cgpa(@PathVariable Long studentId) {
        return resultService.cgpa(studentId);
    }

    @GetMapping("/{id:\\d+}")
    public ResultResponse get(@PathVariable Long id) {
        return resultService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultResponse create(@Valid @RequestBody ResultRequest request) {
        return resultService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public ResultResponse update(@PathVariable Long id, @Valid @RequestBody ResultRequest request) {
        return resultService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        resultService.delete(id);
    }
}

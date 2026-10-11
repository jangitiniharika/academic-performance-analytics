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

import com.academic.performance.dto.FacultyRequest;
import com.academic.performance.dto.FacultyResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.service.FacultyService;

/** REST endpoints for Faculty records (management view: ADMIN + FACULTY). */
@RestController
@RequestMapping("/api/faculty")
public class FacultyController {

    private final FacultyService facultyService;

    public FacultyController(FacultyService facultyService) {
        this.facultyService = facultyService;
    }

    @GetMapping
    public PageResponse<FacultyResponse> list(@RequestParam(required = false) Long departmentId,
                                              @RequestParam(required = false) String search,
                                              @PageableDefault(size = 20, sort = "employeeCode") Pageable pageable) {
        return facultyService.list(departmentId, search, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public FacultyResponse get(@PathVariable Long id) {
        return facultyService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FacultyResponse create(@Valid @RequestBody FacultyRequest request) {
        return facultyService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public FacultyResponse update(@PathVariable Long id, @Valid @RequestBody FacultyRequest request) {
        return facultyService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        facultyService.delete(id);
    }
}

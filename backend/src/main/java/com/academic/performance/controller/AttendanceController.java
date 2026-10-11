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

import com.academic.performance.dto.AttendancePercentageResponse;
import com.academic.performance.dto.AttendanceRequest;
import com.academic.performance.dto.AttendanceResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.service.AttendanceService;

/** REST endpoints for Attendance (Stage 5). Restricted to ADMIN + FACULTY. */
@RestController
@RequestMapping("/api/attendance")
public class AttendanceController {

    private final AttendanceService attendanceService;

    public AttendanceController(AttendanceService attendanceService) {
        this.attendanceService = attendanceService;
    }

    @GetMapping
    public PageResponse<AttendanceResponse> list(@RequestParam(required = false) Long studentId,
                                                 @RequestParam(required = false) Long subjectId,
                                                 @PageableDefault(size = 20, sort = "classDate") Pageable pageable) {
        return attendanceService.list(studentId, subjectId, pageable);
    }

    /** Overall + per-subject attendance percentage for one student. */
    @GetMapping("/percentage/{studentId:\\d+}")
    public AttendancePercentageResponse percentage(@PathVariable Long studentId) {
        return attendanceService.percentageForStudent(studentId);
    }

    @GetMapping("/{id:\\d+}")
    public AttendanceResponse get(@PathVariable Long id) {
        return attendanceService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AttendanceResponse create(@Valid @RequestBody AttendanceRequest request) {
        return attendanceService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public AttendanceResponse update(@PathVariable Long id, @Valid @RequestBody AttendanceRequest request) {
        return attendanceService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        attendanceService.delete(id);
    }
}

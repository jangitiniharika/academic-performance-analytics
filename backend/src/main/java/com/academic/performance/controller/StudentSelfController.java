package com.academic.performance.controller;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.academic.performance.dto.StudentResponse;
import com.academic.performance.service.StudentService;

/**
 * Student self-service endpoints.
 *
 * <p>The whole {@code /api/student/**} namespace is restricted to the STUDENT role
 * in {@code SecurityConfig}, so a student can only ever read their own record.
 */
@RestController
@RequestMapping("/api/student")
public class StudentSelfController {

    private final StudentService studentService;

    public StudentSelfController(StudentService studentService) {
        this.studentService = studentService;
    }

    /** Returns the profile linked to the logged-in student account. */
    @GetMapping("/me")
    public StudentResponse me(Authentication authentication) {
        return studentService.findByUsername(authentication.getName());
    }
}

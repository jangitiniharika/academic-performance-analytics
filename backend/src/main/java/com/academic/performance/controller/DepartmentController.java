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

import com.academic.performance.dto.DepartmentRequest;
import com.academic.performance.dto.DepartmentResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.service.DepartmentService;

/**
 * REST endpoints for Departments.
 *
 * <table border="1">
 *   <caption>Departments</caption>
 *   <tr><th>Method</th><th>Path</th><th>Body</th><th>Success</th></tr>
 *   <tr><td>GET</td><td>/api/departments?search=&page=&size=&sort=</td><td>-</td><td>200</td></tr>
 *   <tr><td>GET</td><td>/api/departments/{id}</td><td>-</td><td>200</td></tr>
 *   <tr><td>POST</td><td>/api/departments</td><td>DepartmentRequest</td><td>201</td></tr>
 *   <tr><td>PUT</td><td>/api/departments/{id}</td><td>DepartmentRequest</td><td>200</td></tr>
 *   <tr><td>DELETE</td><td>/api/departments/{id}</td><td>-</td><td>204</td></tr>
 * </table>
 *
 * <p>Reads are allowed for any authenticated user (reference data); writes are
 * restricted to ADMIN by {@code SecurityConfig}.
 */
@RestController
@RequestMapping("/api/departments")
public class DepartmentController {

    private final DepartmentService departmentService;

    public DepartmentController(DepartmentService departmentService) {
        this.departmentService = departmentService;
    }

    @GetMapping
    public PageResponse<DepartmentResponse> list(@RequestParam(required = false) String search,
                                                 @PageableDefault(size = 20, sort = "name") Pageable pageable) {
        return departmentService.list(search, pageable);
    }

    @GetMapping("/{id:\\d+}")
    public DepartmentResponse get(@PathVariable Long id) {
        return departmentService.get(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DepartmentResponse create(@Valid @RequestBody DepartmentRequest request) {
        return departmentService.create(request);
    }

    @PutMapping("/{id:\\d+}")
    public DepartmentResponse update(@PathVariable Long id, @Valid @RequestBody DepartmentRequest request) {
        return departmentService.update(id, request);
    }

    @DeleteMapping("/{id:\\d+}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        departmentService.delete(id);
    }
}

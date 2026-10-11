package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.DepartmentRequest;
import com.academic.performance.dto.DepartmentResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.entity.Department;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.mapper.DepartmentMapper;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.util.TextUtils;

/** Business logic for Departments (Stage 4). */
@Service
public class DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;

    public DepartmentService(DepartmentRepository departmentRepository, DepartmentMapper departmentMapper) {
        this.departmentRepository = departmentRepository;
        this.departmentMapper = departmentMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<DepartmentResponse> list(String search, Pageable pageable) {
        Page<Department> page = departmentRepository.search(TextUtils.normalizeSearch(search), pageable);
        return PageResponse.from(page.map(departmentMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public DepartmentResponse get(Long id) {
        return departmentMapper.toResponse(getEntity(id));
    }

    @Transactional
    public DepartmentResponse create(DepartmentRequest request) {
        String code = request.getCode().trim();
        if (departmentRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Department code", code);
        }
        Department department = new Department(request.getName().trim(), code);
        department.setDescription(TextUtils.trimToNull(request.getDescription()));
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Transactional
    public DepartmentResponse update(Long id, DepartmentRequest request) {
        Department department = getEntity(id);
        String code = request.getCode().trim();
        // Only treat it as a duplicate if the code actually changed.
        if (!department.getCode().equalsIgnoreCase(code) && departmentRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Department code", code);
        }
        department.setName(request.getName().trim());
        department.setCode(code);
        department.setDescription(TextUtils.trimToNull(request.getDescription()));
        return departmentMapper.toResponse(departmentRepository.save(department));
    }

    @Transactional
    public void delete(Long id) {
        departmentRepository.delete(getEntity(id));
    }

    /** Loads a department or throws a 404-mapped exception. */
    @Transactional(readOnly = true)
    public Department getEntity(Long id) {
        return departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", id));
    }
}

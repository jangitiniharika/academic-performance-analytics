package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.FacultyRequest;
import com.academic.performance.dto.FacultyResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.entity.Department;
import com.academic.performance.entity.Faculty;
import com.academic.performance.entity.User;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.mapper.FacultyMapper;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.repository.FacultyRepository;
import com.academic.performance.repository.UserRepository;
import com.academic.performance.util.TextUtils;

/** Business logic for Faculty members (Stage 4). */
@Service
public class FacultyService {

    private final FacultyRepository facultyRepository;
    private final DepartmentRepository departmentRepository;
    private final UserRepository userRepository;
    private final FacultyMapper facultyMapper;

    public FacultyService(FacultyRepository facultyRepository,
                          DepartmentRepository departmentRepository,
                          UserRepository userRepository,
                          FacultyMapper facultyMapper) {
        this.facultyRepository = facultyRepository;
        this.departmentRepository = departmentRepository;
        this.userRepository = userRepository;
        this.facultyMapper = facultyMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<FacultyResponse> list(Long departmentId, String search, Pageable pageable) {
        Page<Faculty> page = facultyRepository.search(departmentId, TextUtils.normalizeSearch(search), pageable);
        return PageResponse.from(page.map(facultyMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public FacultyResponse get(Long id) {
        return facultyMapper.toResponse(getEntity(id));
    }

    @Transactional
    public FacultyResponse create(FacultyRequest request) {
        String employeeCode = request.getEmployeeCode().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (facultyRepository.existsByEmployeeCode(employeeCode)) {
            throw new DuplicateResourceException("Employee code", employeeCode);
        }
        if (facultyRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Faculty email", email);
        }

        Faculty faculty = new Faculty();
        applyRequest(faculty, request, employeeCode, email);
        return facultyMapper.toResponse(facultyRepository.save(faculty));
    }

    @Transactional
    public FacultyResponse update(Long id, FacultyRequest request) {
        Faculty faculty = getEntity(id);
        String employeeCode = request.getEmployeeCode().trim();
        String email = request.getEmail().trim().toLowerCase();

        if (!faculty.getEmployeeCode().equalsIgnoreCase(employeeCode)
                && facultyRepository.existsByEmployeeCode(employeeCode)) {
            throw new DuplicateResourceException("Employee code", employeeCode);
        }
        if (!faculty.getEmail().equalsIgnoreCase(email) && facultyRepository.existsByEmail(email)) {
            throw new DuplicateResourceException("Faculty email", email);
        }

        applyRequest(faculty, request, employeeCode, email);
        return facultyMapper.toResponse(facultyRepository.save(faculty));
    }

    @Transactional
    public void delete(Long id) {
        facultyRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Faculty getEntity(Long id) {
        return facultyRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", id));
    }

    private void applyRequest(Faculty faculty, FacultyRequest request, String employeeCode, String email) {
        faculty.setEmployeeCode(employeeCode);
        faculty.setFirstName(request.getFirstName().trim());
        faculty.setLastName(request.getLastName().trim());
        faculty.setEmail(email);
        faculty.setPhone(TextUtils.trimToNull(request.getPhone()));
        faculty.setDesignation(TextUtils.trimToNull(request.getDesignation()));
        faculty.setDepartment(resolveDepartment(request.getDepartmentId()));
        faculty.setUser(resolveUser(request.getUserId()));
    }

    private Department resolveDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }

    private User resolveUser(Long userId) {
        if (userId == null) {
            return null;
        }
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));
    }
}

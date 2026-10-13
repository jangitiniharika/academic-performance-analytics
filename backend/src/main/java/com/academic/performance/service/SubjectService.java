package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.SubjectRequest;
import com.academic.performance.dto.SubjectResponse;
import com.academic.performance.entity.Department;
import com.academic.performance.entity.Faculty;
import com.academic.performance.entity.Subject;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.mapper.SubjectMapper;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.repository.FacultyRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.util.TextUtils;

/** Business logic for Subjects (Stage 4). */
@Service
public class SubjectService {

    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectMapper subjectMapper;

    public SubjectService(SubjectRepository subjectRepository,
                          DepartmentRepository departmentRepository,
                          FacultyRepository facultyRepository,
                          SubjectMapper subjectMapper) {
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.facultyRepository = facultyRepository;
        this.subjectMapper = subjectMapper;
    }

    @Transactional(readOnly = true)
    public PageResponse<SubjectResponse> list(Long departmentId, Long facultyId, Integer semester,
                                              String search, Pageable pageable) {
        Page<Subject> page = subjectRepository.search(
                departmentId, facultyId, semester, TextUtils.normalizeSearch(search), pageable);
        return PageResponse.from(page.map(subjectMapper::toResponse));
    }

    @Transactional(readOnly = true)
    public SubjectResponse get(Long id) {
        return subjectMapper.toResponse(getEntity(id));
    }

    @Transactional
    public SubjectResponse create(SubjectRequest request) {
        String code = request.getCode().trim();
        if (subjectRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Subject code", code);
        }
        Subject subject = new Subject();
        subject.setCode(code);
        subject.setName(request.getName().trim());
        subject.setSemester(request.getSemester());
        subject.setCredits(request.getCredits());
        subject.setDepartment(resolveDepartment(request.getDepartmentId()));
        subject.setFaculty(resolveFaculty(request.getFacultyId()));
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public SubjectResponse update(Long id, SubjectRequest request) {
        Subject subject = getEntity(id);
        String code = request.getCode().trim();
        if (!subject.getCode().equalsIgnoreCase(code) && subjectRepository.existsByCode(code)) {
            throw new DuplicateResourceException("Subject code", code);
        }
        subject.setCode(code);
        subject.setName(request.getName().trim());
        subject.setSemester(request.getSemester());
        subject.setCredits(request.getCredits());
        subject.setDepartment(resolveDepartment(request.getDepartmentId()));
        subject.setFaculty(resolveFaculty(request.getFacultyId()));
        return subjectMapper.toResponse(subjectRepository.save(subject));
    }

    @Transactional
    public void delete(Long id) {
        subjectRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Subject getEntity(Long id) {
        return subjectRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", id));
    }

    private Department resolveDepartment(Long departmentId) {
        return departmentRepository.findById(departmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Department", "id", departmentId));
    }

    private Faculty resolveFaculty(Long facultyId) {
        if (facultyId == null) {
            return null;
        }
        return facultyRepository.findById(facultyId)
                .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", facultyId));
    }
}

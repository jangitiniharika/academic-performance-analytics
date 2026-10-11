package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.AssignmentRequest;
import com.academic.performance.dto.AssignmentResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.entity.Assignment;
import com.academic.performance.entity.Faculty;
import com.academic.performance.entity.Subject;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AssignmentRepository;
import com.academic.performance.repository.FacultyRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.util.TextUtils;

/** Business logic for Assignments (Stage 7). */
@Service
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final SubjectRepository subjectRepository;
    private final FacultyRepository facultyRepository;

    public AssignmentService(AssignmentRepository assignmentRepository,
                             SubjectRepository subjectRepository,
                             FacultyRepository facultyRepository) {
        this.assignmentRepository = assignmentRepository;
        this.subjectRepository = subjectRepository;
        this.facultyRepository = facultyRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AssignmentResponse> list(Long subjectId, Long facultyId, Pageable pageable) {
        Page<Assignment> page = assignmentRepository.search(subjectId, facultyId, pageable);
        return PageResponse.from(page.map(AssignmentResponse::of));
    }

    @Transactional(readOnly = true)
    public AssignmentResponse get(Long id) {
        return AssignmentResponse.of(getEntity(id));
    }

    @Transactional
    public AssignmentResponse create(AssignmentRequest request) {
        Assignment assignment = new Assignment();
        apply(assignment, request);
        return AssignmentResponse.of(assignmentRepository.save(assignment));
    }

    @Transactional
    public AssignmentResponse update(Long id, AssignmentRequest request) {
        Assignment assignment = getEntity(id);
        apply(assignment, request);
        return AssignmentResponse.of(assignmentRepository.save(assignment));
    }

    @Transactional
    public void delete(Long id) {
        assignmentRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Assignment getEntity(Long id) {
        return assignmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", id));
    }

    private void apply(Assignment assignment, AssignmentRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.subjectId()));
        Faculty faculty = null;
        if (request.facultyId() != null) {
            faculty = facultyRepository.findById(request.facultyId())
                    .orElseThrow(() -> new ResourceNotFoundException("Faculty", "id", request.facultyId()));
        }
        assignment.setTitle(request.title().trim());
        assignment.setDescription(TextUtils.trimToNull(request.description()));
        assignment.setSubject(subject);
        assignment.setFaculty(faculty);
        assignment.setDueDate(request.dueDate());
        assignment.setMaxMarks(request.maxMarks());
    }
}

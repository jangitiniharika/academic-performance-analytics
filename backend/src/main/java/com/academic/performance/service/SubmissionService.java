package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.SubmissionRequest;
import com.academic.performance.dto.SubmissionResponse;
import com.academic.performance.entity.Assignment;
import com.academic.performance.entity.AssignmentSubmission;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.SubmissionStatus;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AssignmentRepository;
import com.academic.performance.repository.AssignmentSubmissionRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.util.TextUtils;

/**
 * Business logic for Assignment submissions (Stage 7).
 *
 * <p>There is at most one submission per {@code (assignment, student)}. A new
 * submission starts as {@code PENDING}; the student "submit" action sets
 * {@code SUBMITTED} (or {@code LATE}) and a teacher grading it sets {@code GRADED}
 * together with the marks.
 */
@Service
public class SubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final StudentRepository studentRepository;

    public SubmissionService(AssignmentSubmissionRepository submissionRepository,
                             AssignmentRepository assignmentRepository,
                             StudentRepository studentRepository) {
        this.submissionRepository = submissionRepository;
        this.assignmentRepository = assignmentRepository;
        this.studentRepository = studentRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<SubmissionResponse> list(Long assignmentId, Long studentId, Pageable pageable) {
        Page<AssignmentSubmission> page = submissionRepository.search(assignmentId, studentId, pageable);
        return PageResponse.from(page.map(SubmissionResponse::of));
    }

    @Transactional(readOnly = true)
    public SubmissionResponse get(Long id) {
        return SubmissionResponse.of(getEntity(id));
    }

    @Transactional
    public SubmissionResponse create(SubmissionRequest request) {
        Assignment assignment = resolveAssignment(request.assignmentId());
        Student student = resolveStudent(request.studentId());

        submissionRepository.findByAssignmentIdAndStudentId(assignment.getId(), student.getId())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException("Submission for assignment", assignment.getTitle());
                });

        AssignmentSubmission submission = new AssignmentSubmission();
        submission.setAssignment(assignment);
        submission.setStudent(student);
        submission.setStatus(request.status() == null ? SubmissionStatus.PENDING : request.status());
        submission.setMarks(request.marks());
        submission.setFeedback(TextUtils.trimToNull(request.feedback()));
        return SubmissionResponse.of(submissionRepository.save(submission));
    }

    @Transactional
    public SubmissionResponse update(Long id, SubmissionRequest request) {
        AssignmentSubmission submission = getEntity(id);
        if (request.status() != null) {
            submission.setStatus(request.status());
        }
        submission.setMarks(request.marks());
        submission.setFeedback(TextUtils.trimToNull(request.feedback()));
        return SubmissionResponse.of(submissionRepository.save(submission));
    }

    @Transactional
    public void delete(Long id) {
        submissionRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public AssignmentSubmission getEntity(Long id) {
        return submissionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment submission", "id", id));
    }

    private Assignment resolveAssignment(Long assignmentId) {
        return assignmentRepository.findById(assignmentId)
                .orElseThrow(() -> new ResourceNotFoundException("Assignment", "id", assignmentId));
    }

    private Student resolveStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }
}

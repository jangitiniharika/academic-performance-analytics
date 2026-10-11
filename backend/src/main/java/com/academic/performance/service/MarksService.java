package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.MarksRequest;
import com.academic.performance.dto.MarksResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.entity.Exam;
import com.academic.performance.entity.Marks;
import com.academic.performance.entity.Student;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.ExamRepository;
import com.academic.performance.repository.MarksRepository;
import com.academic.performance.repository.StudentRepository;

/**
 * Business logic for Marks (Stage 6).
 *
 * <p>The {@code percentage} column is filled automatically by the entity's
 * {@code @PrePersist}/{@code @PreUpdate} callback, so it always matches the
 * current marks and the exam's maximum.
 */
@Service
public class MarksService {

    private final MarksRepository marksRepository;
    private final StudentRepository studentRepository;
    private final ExamRepository examRepository;

    public MarksService(MarksRepository marksRepository,
                        StudentRepository studentRepository,
                        ExamRepository examRepository) {
        this.marksRepository = marksRepository;
        this.studentRepository = studentRepository;
        this.examRepository = examRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<MarksResponse> list(Long studentId, Long examId, Long subjectId, Pageable pageable) {
        Page<Marks> page = marksRepository.search(studentId, examId, subjectId, pageable);
        return PageResponse.from(page.map(MarksResponse::of));
    }

    @Transactional(readOnly = true)
    public MarksResponse get(Long id) {
        return MarksResponse.of(getEntity(id));
    }

    @Transactional
    public MarksResponse create(MarksRequest request) {
        Student student = resolveStudent(request.studentId());
        Exam exam = resolveExam(request.examId());

        marksRepository.findByStudentIdAndExamId(student.getId(), exam.getId()).ifPresent(existing -> {
            throw new DuplicateResourceException("Marks for exam", exam.getName());
        });

        Marks marks = new Marks();
        marks.setStudent(student);
        marks.setExam(exam);
        marks.setMarksObtained(request.marksObtained());
        return MarksResponse.of(marksRepository.save(marks));
    }

    @Transactional
    public MarksResponse update(Long id, MarksRequest request) {
        Marks marks = getEntity(id);
        Student student = resolveStudent(request.studentId());
        Exam exam = resolveExam(request.examId());

        boolean keyChanged = !marks.getStudent().getId().equals(student.getId())
                || !marks.getExam().getId().equals(exam.getId());
        if (keyChanged && marksRepository.findByStudentIdAndExamId(student.getId(), exam.getId()).isPresent()) {
            throw new DuplicateResourceException("Marks for exam", exam.getName());
        }

        marks.setStudent(student);
        marks.setExam(exam);
        marks.setMarksObtained(request.marksObtained());
        return MarksResponse.of(marksRepository.save(marks));
    }

    @Transactional
    public void delete(Long id) {
        marksRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Marks getEntity(Long id) {
        return marksRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Marks", "id", id));
    }

    private Student resolveStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }

    private Exam resolveExam(Long examId) {
        return examRepository.findById(examId)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", examId));
    }
}

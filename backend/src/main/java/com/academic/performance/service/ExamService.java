package com.academic.performance.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.ExamRequest;
import com.academic.performance.dto.ExamResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.entity.Exam;
import com.academic.performance.entity.Subject;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.ExamRepository;
import com.academic.performance.repository.SubjectRepository;

/** Business logic for Exams (Stage 6). */
@Service
public class ExamService {

    private final ExamRepository examRepository;
    private final SubjectRepository subjectRepository;

    public ExamService(ExamRepository examRepository, SubjectRepository subjectRepository) {
        this.examRepository = examRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ExamResponse> list(Long subjectId, Pageable pageable) {
        Page<Exam> page = examRepository.search(subjectId, pageable);
        return PageResponse.from(page.map(ExamResponse::of));
    }

    @Transactional(readOnly = true)
    public ExamResponse get(Long id) {
        return ExamResponse.of(getEntity(id));
    }

    @Transactional
    public ExamResponse create(ExamRequest request) {
        Exam exam = new Exam();
        apply(exam, request);
        return ExamResponse.of(examRepository.save(exam));
    }

    @Transactional
    public ExamResponse update(Long id, ExamRequest request) {
        Exam exam = getEntity(id);
        apply(exam, request);
        return ExamResponse.of(examRepository.save(exam));
    }

    @Transactional
    public void delete(Long id) {
        examRepository.delete(getEntity(id));
    }

    @Transactional(readOnly = true)
    public Exam getEntity(Long id) {
        return examRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Exam", "id", id));
    }

    private void apply(Exam exam, ExamRequest request) {
        Subject subject = subjectRepository.findById(request.subjectId())
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", request.subjectId()));
        exam.setName(request.name().trim());
        exam.setSubject(subject);
        exam.setExamDate(request.examDate());
        exam.setMaxMarks(request.maxMarks());
    }
}

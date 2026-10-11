package com.academic.performance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.CgpaResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.ResultRequest;
import com.academic.performance.dto.ResultResponse;
import com.academic.performance.entity.AcademicResult;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.Subject;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AcademicResultRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;

/**
 * Business logic for Academic results and CGPA (Stage 8).
 *
 * <p>Whenever a result changes the owning student's {@code cgpa} column is
 * recomputed, so the cached value on {@link Student} always matches
 * CGPA = sum(gradePoints x credits) / sum(credits).
 */
@Service
public class ResultService {

    private final AcademicResultRepository resultRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public ResultService(AcademicResultRepository resultRepository,
                         StudentRepository studentRepository,
                         SubjectRepository subjectRepository) {
        this.resultRepository = resultRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<ResultResponse> list(Long studentId, Integer semester, Pageable pageable) {
        Page<AcademicResult> page = resultRepository.search(studentId, semester, pageable);
        return PageResponse.from(page.map(ResultResponse::of));
    }

    @Transactional(readOnly = true)
    public ResultResponse get(Long id) {
        return ResultResponse.of(getEntity(id));
    }

    @Transactional
    public ResultResponse create(ResultRequest request) {
        Student student = resolveStudent(request.studentId());
        Subject subject = resolveSubject(request.subjectId());

        if (resultRepository.existsByStudentIdAndSubjectIdAndSemester(
                student.getId(), subject.getId(), request.semester())) {
            throw new DuplicateResourceException(
                    "Result for " + subject.getCode() + " in semester " + request.semester(),
                    student.getRollNumber());
        }

        AcademicResult result = new AcademicResult();
        apply(result, student, subject, request);
        AcademicResult saved = resultRepository.save(result);
        recomputeCgpa(student);
        return ResultResponse.of(saved);
    }

    @Transactional
    public ResultResponse update(Long id, ResultRequest request) {
        AcademicResult result = getEntity(id);
        Student previousStudent = result.getStudent();
        Student student = resolveStudent(request.studentId());
        Subject subject = resolveSubject(request.subjectId());

        boolean keyChanged = !result.getStudent().getId().equals(student.getId())
                || !result.getSubject().getId().equals(subject.getId())
                || !result.getSemester().equals(request.semester());

        if (keyChanged && resultRepository.existsByStudentIdAndSubjectIdAndSemester(
                student.getId(), subject.getId(), request.semester())) {
            throw new DuplicateResourceException(
                    "Result for " + subject.getCode() + " in semester " + request.semester(),
                    student.getRollNumber());
        }

        apply(result, student, subject, request);
        AcademicResult saved = resultRepository.save(result);
        recomputeCgpa(student);
        if (previousStudent != null && !previousStudent.getId().equals(student.getId())) {
            recomputeCgpa(resolveStudent(previousStudent.getId()));
        }
        return ResultResponse.of(saved);
    }

    @Transactional
    public void delete(Long id) {
        AcademicResult result = getEntity(id);
        Student student = result.getStudent();
        resultRepository.delete(result);
        recomputeCgpa(student);
    }

    @Transactional(readOnly = true)
    public CgpaResponse cgpa(Long studentId) {
        Student student = resolveStudent(studentId);
        return buildCgpa(student, resultRepository.findByStudentId(studentId));
    }

    @Transactional(readOnly = true)
    public AcademicResult getEntity(Long id) {
        return resultRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Academic result", "id", id));
    }

    // --------------------------------------------------------------- helpers

    private void apply(AcademicResult result, Student student, Subject subject, ResultRequest request) {
        result.setStudent(student);
        result.setSubject(subject);
        result.setSemester(request.semester());
        result.setGrade(request.grade().trim().toUpperCase());
        result.setGradePoints(request.gradePoints());
        result.setCredits(request.credits());
    }

    /** Recomputes and stores the cached CGPA on the student. */
    private void recomputeCgpa(Student student) {
        BigDecimal cgpa = computeCgpa(resultRepository.findByStudentId(student.getId()));
        student.setCgpa(cgpa);
        studentRepository.save(student);
    }

    private BigDecimal computeCgpa(List<AcademicResult> results) {
        BigDecimal totalPoints = BigDecimal.ZERO;
        int totalCredits = 0;
        for (AcademicResult result : results) {
            int credits = result.getCredits() == null ? 0 : result.getCredits();
            BigDecimal gradePoints = result.getGradePoints() == null ? BigDecimal.ZERO : result.getGradePoints();
            totalPoints = totalPoints.add(gradePoints.multiply(BigDecimal.valueOf(credits)));
            totalCredits += credits;
        }
        if (totalCredits == 0) {
            return null;
        }
        return totalPoints.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
    }

    private CgpaResponse buildCgpa(Student student, List<AcademicResult> results) {
        BigDecimal totalPoints = BigDecimal.ZERO;
        int totalCredits = 0;
        for (AcademicResult result : results) {
            int credits = result.getCredits() == null ? 0 : result.getCredits();
            BigDecimal gradePoints = result.getGradePoints() == null ? BigDecimal.ZERO : result.getGradePoints();
            totalPoints = totalPoints.add(gradePoints.multiply(BigDecimal.valueOf(credits)));
            totalCredits += credits;
        }
        BigDecimal cgpa = totalCredits == 0
                ? null
                : totalPoints.divide(BigDecimal.valueOf(totalCredits), 2, RoundingMode.HALF_UP);
        return new CgpaResponse(student.getId(), student.getRollNumber(), cgpa,
                totalCredits, totalPoints, results.size());
    }

    private Student resolveStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }

    private Subject resolveSubject(Long subjectId) {
        return subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));
    }
}

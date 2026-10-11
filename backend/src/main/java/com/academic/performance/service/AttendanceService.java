package com.academic.performance.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.AttendancePercentageResponse;
import com.academic.performance.dto.AttendanceRequest;
import com.academic.performance.dto.AttendanceResponse;
import com.academic.performance.dto.PageResponse;
import com.academic.performance.dto.SubjectAttendancePercentage;
import com.academic.performance.dto.SubjectSummary;
import com.academic.performance.entity.Attendance;
import com.academic.performance.entity.AttendanceStatus;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.Subject;
import com.academic.performance.exception.DuplicateResourceException;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AttendanceRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.util.TextUtils;

/**
 * Business logic for Attendance (Stage 5).
 *
 * <p>A row is unique per {@code (student, subject, date)}; the service returns a
 * clear {@code 409} when that combination already exists. Percentages are always
 * derived through {@link AcademicCalculator} so "no data" stays {@code null}.
 */
@Service
public class AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final StudentRepository studentRepository;
    private final SubjectRepository subjectRepository;

    public AttendanceService(AttendanceRepository attendanceRepository,
                             StudentRepository studentRepository,
                             SubjectRepository subjectRepository) {
        this.attendanceRepository = attendanceRepository;
        this.studentRepository = studentRepository;
        this.subjectRepository = subjectRepository;
    }

    @Transactional(readOnly = true)
    public PageResponse<AttendanceResponse> list(Long studentId, Long subjectId, Pageable pageable) {
        Page<Attendance> page = attendanceRepository.search(studentId, subjectId, pageable);
        return PageResponse.from(page.map(AttendanceResponse::of));
    }

    @Transactional(readOnly = true)
    public AttendanceResponse get(Long id) {
        return AttendanceResponse.of(getEntity(id));
    }

    @Transactional
    public AttendanceResponse create(AttendanceRequest request) {
        Student student = resolveStudent(request.studentId());
        Subject subject = resolveSubject(request.subjectId());

        attendanceRepository
                .findByStudentIdAndSubjectIdAndClassDate(student.getId(), subject.getId(), request.classDate())
                .ifPresent(existing -> {
                    throw new DuplicateResourceException(
                            "Attendance for " + subject.getCode() + " on " + request.classDate(),
                            student.getRollNumber());
                });

        Attendance attendance = new Attendance();
        apply(attendance, student, subject, request);
        return AttendanceResponse.of(attendanceRepository.save(attendance));
    }

    @Transactional
    public AttendanceResponse update(Long id, AttendanceRequest request) {
        Attendance attendance = getEntity(id);
        Student student = resolveStudent(request.studentId());
        Subject subject = resolveSubject(request.subjectId());

        boolean keyChanged = !attendance.getStudent().getId().equals(student.getId())
                || !attendance.getSubject().getId().equals(subject.getId())
                || !attendance.getClassDate().equals(request.classDate());

        if (keyChanged && attendanceRepository
                .findByStudentIdAndSubjectIdAndClassDate(student.getId(), subject.getId(), request.classDate())
                .isPresent()) {
            throw new DuplicateResourceException(
                    "Attendance for " + subject.getCode() + " on " + request.classDate(),
                    student.getRollNumber());
        }

        apply(attendance, student, subject, request);
        return AttendanceResponse.of(attendanceRepository.save(attendance));
    }

    @Transactional
    public void delete(Long id) {
        attendanceRepository.delete(getEntity(id));
    }

    /**
     * Overall and per-subject attendance percentages for one student.
     *
     * <p>A session counts as "attended" unless the status is {@code ABSENT}, so
     * {@code LATE} still counts - a student who arrives late was present.
     */
    @Transactional(readOnly = true)
    public AttendancePercentageResponse percentageForStudent(Long studentId) {
        Student student = resolveStudent(studentId);
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);

        long total = records.size();
        long attended = records.stream().filter(a -> a.getStatus() != AttendanceStatus.ABSENT).count();
        BigDecimal overall = percent(attended, total);

        Map<Long, List<Attendance>> grouped = new LinkedHashMap<>();
        for (Attendance record : records) {
            if (record.getSubject() != null) {
                grouped.computeIfAbsent(record.getSubject().getId(), key -> new ArrayList<>()).add(record);
            }
        }

        List<SubjectAttendancePercentage> bySubject = new ArrayList<>();
        for (List<Attendance> list : grouped.values()) {
            Subject subject = list.get(0).getSubject();
            long subjectTotal = list.size();
            long subjectAttended = list.stream().filter(a -> a.getStatus() != AttendanceStatus.ABSENT).count();
            bySubject.add(new SubjectAttendancePercentage(
                    SubjectSummary.of(subject), subjectAttended, subjectTotal,
                    percent(subjectAttended, subjectTotal)));
        }
        bySubject.sort(Comparator.comparing(p -> p.subject() == null ? "" : p.subject().code()));

        return new AttendancePercentageResponse(
                student.getId(), student.getRollNumber(), attended, total, overall, bySubject);
    }

    // --------------------------------------------------------------- helpers

    private void apply(Attendance attendance, Student student, Subject subject, AttendanceRequest request) {
        attendance.setStudent(student);
        attendance.setSubject(subject);
        attendance.setClassDate(request.classDate());
        attendance.setStatus(request.status());
        attendance.setRemarks(TextUtils.trimToNull(request.remarks()));
    }

    private BigDecimal percent(long attended, long total) {
        return AcademicCalculator.attendancePercentage(attended, total).orElse(null);
    }

    @Transactional(readOnly = true)
    public Attendance getEntity(Long id) {
        return attendanceRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attendance", "id", id));
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

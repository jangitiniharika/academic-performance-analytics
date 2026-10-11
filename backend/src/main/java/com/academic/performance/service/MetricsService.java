package com.academic.performance.service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.entity.AssignmentSubmission;
import com.academic.performance.entity.Attendance;
import com.academic.performance.entity.AttendanceStatus;
import com.academic.performance.entity.Marks;
import com.academic.performance.entity.Student;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AssignmentSubmissionRepository;
import com.academic.performance.repository.AttendanceRepository;
import com.academic.performance.repository.MarksRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.util.Numbers;

/**
 * Computes the four indicators used by analytics and prediction (Stage 9/10):
 * CGPA, attendance %, average internal marks % and assignment %.
 *
 * <p>Every indicator is {@code null} when the student has no data for it, so the
 * prediction can honestly report "missing" instead of inventing a zero.
 */
@Service
public class MetricsService {

    private final StudentRepository studentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final AssignmentSubmissionRepository submissionRepository;

    public MetricsService(StudentRepository studentRepository,
                          AttendanceRepository attendanceRepository,
                          MarksRepository marksRepository,
                          AssignmentSubmissionRepository submissionRepository) {
        this.studentRepository = studentRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
        this.submissionRepository = submissionRepository;
    }

    /** The four normalised indicators for one student. */
    public record StudentMetrics(
            BigDecimal cgpa,
            BigDecimal attendancePercentage,
            BigDecimal averageMarksPercentage,
            BigDecimal assignmentPercentage) {
    }

    @Transactional(readOnly = true)
    public StudentMetrics forStudent(Long studentId) {
        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
        return forStudent(student);
    }

    @Transactional(readOnly = true)
    public StudentMetrics forStudent(Student student) {
        return new StudentMetrics(
                student.getCgpa(),
                attendancePercentage(student.getId()),
                averageMarksPercentage(student.getId()),
                assignmentPercentage(student.getId()));
    }

    /** Attendance % (a LATE arrival still counts as attended). */
    @Transactional(readOnly = true)
    public BigDecimal attendancePercentage(Long studentId) {
        List<Attendance> records = attendanceRepository.findByStudentId(studentId);
        long total = records.size();
        long attended = records.stream().filter(a -> a.getStatus() != AttendanceStatus.ABSENT).count();
        return com.academic.performance.service.AcademicCalculator
                .attendancePercentage(attended, total).orElse(null);
    }

    /** Average of the stored marks percentages across every exam. */
    @Transactional(readOnly = true)
    public BigDecimal averageMarksPercentage(Long studentId) {
        List<Marks> marks = marksRepository.findByStudentId(studentId);
        return Numbers.average(marks.stream().map(Marks::getPercentage).toList());
    }

    /** Average of (submission marks / assignment maximum) x 100 across graded work. */
    @Transactional(readOnly = true)
    public BigDecimal assignmentPercentage(Long studentId) {
        List<AssignmentSubmission> submissions = submissionRepository.findByStudentId(studentId);
        List<BigDecimal> percentages = new ArrayList<>();
        for (AssignmentSubmission submission : submissions) {
            if (submission.getMarks() == null || submission.getAssignment() == null) {
                continue;
            }
            Integer maxMarks = submission.getAssignment().getMaxMarks();
            if (maxMarks == null || maxMarks <= 0) {
                continue;
            }
            percentages.add(submission.getMarks()
                    .multiply(BigDecimal.valueOf(100))
                    .divide(BigDecimal.valueOf(maxMarks), 2, RoundingMode.HALF_UP));
        }
        return Numbers.average(percentages);
    }

    @Transactional(readOnly = true)
    public Student requireStudent(Long studentId) {
        return studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student", "id", studentId));
    }
}

package com.academic.performance.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.academic.performance.dto.AnalyticsOverviewResponse;
import com.academic.performance.dto.ClassAnalyticsResponse;
import com.academic.performance.dto.DistributionBucket;
import com.academic.performance.dto.PredictionResponse;
import com.academic.performance.dto.SubjectPerformance;
import com.academic.performance.dto.StudentAnalyticsResponse;
import com.academic.performance.dto.TrendPoint;
import com.academic.performance.entity.Attendance;
import com.academic.performance.entity.AttendanceStatus;
import com.academic.performance.entity.Marks;
import com.academic.performance.entity.RiskLevel;
import com.academic.performance.entity.Student;
import com.academic.performance.entity.Subject;
import com.academic.performance.exception.ResourceNotFoundException;
import com.academic.performance.repository.AttendanceRepository;
import com.academic.performance.repository.DepartmentRepository;
import com.academic.performance.repository.FacultyRepository;
import com.academic.performance.repository.MarksRepository;
import com.academic.performance.repository.PerformanceAlertRepository;
import com.academic.performance.repository.StudentRepository;
import com.academic.performance.repository.SubjectRepository;
import com.academic.performance.util.Numbers;

/**
 * Analytics aggregation (Stage 9).
 *
 * <p>Reads the raw records written by Stages 5-8 and turns them into the
 * institution overview, a per-student breakdown and a per-class view. Every
 * average is {@code null} (not 0) when there is nothing to average.
 */
@Service
public class AnalyticsService {

    private final StudentRepository studentRepository;
    private final FacultyRepository facultyRepository;
    private final SubjectRepository subjectRepository;
    private final DepartmentRepository departmentRepository;
    private final AttendanceRepository attendanceRepository;
    private final MarksRepository marksRepository;
    private final PerformanceAlertRepository alertRepository;
    private final MetricsService metricsService;
    private final PredictionService predictionService;

    public AnalyticsService(StudentRepository studentRepository,
                            FacultyRepository facultyRepository,
                            SubjectRepository subjectRepository,
                            DepartmentRepository departmentRepository,
                            AttendanceRepository attendanceRepository,
                            MarksRepository marksRepository,
                            PerformanceAlertRepository alertRepository,
                            MetricsService metricsService,
                            PredictionService predictionService) {
        this.studentRepository = studentRepository;
        this.facultyRepository = facultyRepository;
        this.subjectRepository = subjectRepository;
        this.departmentRepository = departmentRepository;
        this.attendanceRepository = attendanceRepository;
        this.marksRepository = marksRepository;
        this.alertRepository = alertRepository;
        this.metricsService = metricsService;
        this.predictionService = predictionService;
    }

    /** Institution-wide summary used by the admin dashboard. */
    @Transactional(readOnly = true)
    public AnalyticsOverviewResponse overview() {
        List<Student> students = studentRepository.findAll();
        List<BigDecimal> attendanceValues = new ArrayList<>();
        List<BigDecimal> marksValues = new ArrayList<>();
        List<BigDecimal> cgpaValues = new ArrayList<>();
        long high = 0;
        long medium = 0;

        for (Student student : students) {
            MetricsService.StudentMetrics metrics = metricsService.forStudent(student);
            attendanceValues.add(metrics.attendancePercentage());
            marksValues.add(metrics.averageMarksPercentage());
            if (student.getCgpa() != null) {
                cgpaValues.add(student.getCgpa());
            }
            RiskLevel risk = predictionService.evaluate(student).riskLevel();
            if (risk == RiskLevel.HIGH) {
                high++;
            } else if (risk == RiskLevel.MEDIUM) {
                medium++;
            }
        }

        return new AnalyticsOverviewResponse(
                students.size(),
                facultyRepository.count(),
                subjectRepository.count(),
                departmentRepository.count(),
                Numbers.average(attendanceValues),
                Numbers.average(marksValues),
                Numbers.average(cgpaValues),
                high,
                medium,
                alertRepository.countByAcknowledgedFalse());
    }

    /** The full analytics picture for one student, including a fresh prediction. */
    @Transactional(readOnly = true)
    public StudentAnalyticsResponse studentAnalytics(Long studentId) {
        Student student = metricsService.requireStudent(studentId);
        MetricsService.StudentMetrics metrics = metricsService.forStudent(student);
        PredictionResponse prediction = predictionService.evaluate(student);

        List<Attendance> attendanceRecords = attendanceRepository.findByStudentId(studentId);
        List<Marks> marksRecords = marksRepository.findByStudentId(studentId);

        Map<Long, Subject> subjects = new LinkedHashMap<>();
        Map<Long, List<Attendance>> attendanceBySubject = new LinkedHashMap<>();
        for (Attendance record : attendanceRecords) {
            if (record.getSubject() != null) {
                subjects.putIfAbsent(record.getSubject().getId(), record.getSubject());
                attendanceBySubject.computeIfAbsent(record.getSubject().getId(), key -> new ArrayList<>()).add(record);
            }
        }
        Map<Long, List<Marks>> marksBySubject = new LinkedHashMap<>();
        for (Marks marks : marksRecords) {
            if (marks.getExam() != null && marks.getExam().getSubject() != null) {
                Subject subject = marks.getExam().getSubject();
                subjects.putIfAbsent(subject.getId(), subject);
                marksBySubject.computeIfAbsent(subject.getId(), key -> new ArrayList<>()).add(marks);
            }
        }

        List<SubjectPerformance> subjectBreakdown = new ArrayList<>();
        for (Subject subject : subjects.values()) {
            List<Attendance> subjectAttendance = attendanceBySubject.getOrDefault(subject.getId(), List.of());
            long total = subjectAttendance.size();
            long attended = subjectAttendance.stream()
                    .filter(a -> a.getStatus() != AttendanceStatus.ABSENT)
                    .count();
            BigDecimal attendancePercentage = AcademicCalculator
                    .attendancePercentage(attended, total).orElse(null);

            List<Marks> subjectMarks = marksBySubject.getOrDefault(subject.getId(), List.of());
            BigDecimal marksPercentage = Numbers.average(
                    subjectMarks.stream().map(Marks::getPercentage).toList());

            subjectBreakdown.add(new SubjectPerformance(
                    subject.getId(), subject.getCode(), subject.getName(),
                    attendancePercentage, marksPercentage, subjectMarks.size()));
        }
        subjectBreakdown.sort(Comparator.comparing(SubjectPerformance::subjectCode));

        List<TrendPoint> trend = marksRecords.stream()
                .filter(m -> m.getExam() != null)
                .map(m -> new TrendPoint(
                        m.getExam().getName(),
                        m.getExam().getSubject() == null ? null : m.getExam().getSubject().getCode(),
                        m.getExam().getExamDate(),
                        m.getPercentage()))
                .sorted(Comparator.comparing(TrendPoint::date,
                        Comparator.nullsLast(Comparator.naturalOrder())))
                .toList();

        String departmentCode = student.getDepartment() == null ? null : student.getDepartment().getCode();

        return new StudentAnalyticsResponse(
                student.getId(),
                student.getRollNumber(),
                student.getFullName(),
                departmentCode,
                metrics.attendancePercentage(),
                metrics.averageMarksPercentage(),
                metrics.assignmentPercentage(),
                student.getCgpa(),
                prediction,
                subjectBreakdown,
                trend);
    }

    /** Class-level analytics for one subject (marks + attendance + distribution). */
    @Transactional(readOnly = true)
    public ClassAnalyticsResponse classAnalytics(Long subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new ResourceNotFoundException("Subject", "id", subjectId));

        List<Marks> subjectMarks = marksRepository.findByExam_SubjectId(subjectId);
        BigDecimal averageMarks = Numbers.average(
                subjectMarks.stream().map(Marks::getPercentage).toList());

        List<Attendance> subjectAttendance = attendanceRepository.findBySubjectId(subjectId);
        long totalSessions = subjectAttendance.size();
        long attended = subjectAttendance.stream()
                .filter(a -> a.getStatus() != AttendanceStatus.ABSENT)
                .count();
        BigDecimal averageAttendance = AcademicCalculator
                .attendancePercentage(attended, totalSessions).orElse(null);

        long studentCount = subject.getDepartment() == null
                ? 0
                : studentRepository.findByDepartmentId(subject.getDepartment().getId()).size();

        // Distribution of each student's average marks in this subject.
        Map<Long, List<BigDecimal>> marksByStudent = new LinkedHashMap<>();
        for (Marks marks : subjectMarks) {
            if (marks.getStudent() != null && marks.getPercentage() != null) {
                marksByStudent.computeIfAbsent(marks.getStudent().getId(), key -> new ArrayList<>())
                        .add(marks.getPercentage());
            }
        }
        List<BigDecimal> perStudentAverages = new ArrayList<>();
        for (List<BigDecimal> values : marksByStudent.values()) {
            perStudentAverages.add(Numbers.average(values));
        }

        return new ClassAnalyticsResponse(
                subject.getId(),
                subject.getCode(),
                subject.getName(),
                studentCount,
                averageMarks,
                averageAttendance,
                distribution(perStudentAverages));
    }

    /** Buckets scores into 0-40 / 40-55 / 55-70 / 70-85 / 85-100. */
    private List<DistributionBucket> distribution(List<BigDecimal> values) {
        String[] labels = { "0-40", "40-55", "55-70", "70-85", "85-100" };
        long[] counts = new long[labels.length];
        for (BigDecimal value : values) {
            if (value == null) {
                continue;
            }
            double score = value.doubleValue();
            int index;
            if (score < 40) {
                index = 0;
            } else if (score < 55) {
                index = 1;
            } else if (score < 70) {
                index = 2;
            } else if (score < 85) {
                index = 3;
            } else {
                index = 4;
            }
            counts[index]++;
        }
        List<DistributionBucket> buckets = new ArrayList<>();
        for (int i = 0; i < labels.length; i++) {
            buckets.add(new DistributionBucket(labels[i], counts[i]));
        }
        return buckets;
    }
}

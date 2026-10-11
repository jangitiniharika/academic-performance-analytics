package com.academic.performance.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Attendance;
import com.academic.performance.entity.AttendanceStatus;

/**
 * Database access for {@link Attendance}.
 *
 * <p>The {@code count...} methods feed the attendance-percentage calculation in
 * the service layer:
 * {@code AcademicCalculator.attendancePercentage(countByStudentIdAndStatus(id, PRESENT), countByStudentId(id))}.
 */
@Repository
public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    List<Attendance> findByStudentId(Long studentId);

    List<Attendance> findBySubjectId(Long subjectId);

    List<Attendance> findByStudentIdAndSubjectId(Long studentId, Long subjectId);

    Optional<Attendance> findByStudentIdAndSubjectIdAndClassDate(
            Long studentId, Long subjectId, LocalDate classDate);

    Page<Attendance> findByStudentId(Long studentId, Pageable pageable);

    long countByStudentId(Long studentId);

    long countByStudentIdAndStatus(Long studentId, AttendanceStatus status);

    long countByStudentIdAndSubjectId(Long studentId, Long subjectId);

    long countByStudentIdAndSubjectIdAndStatus(Long studentId, Long subjectId, AttendanceStatus status);

    /** Paginated, filtered listing; a {@code null} filter is ignored. */
    @Query("""
            select a from Attendance a
            where (:studentId is null or a.student.id = :studentId)
              and (:subjectId is null or a.subject.id = :subjectId)
            """)
    Page<Attendance> search(@Param("studentId") Long studentId,
                            @Param("subjectId") Long subjectId,
                            Pageable pageable);
}

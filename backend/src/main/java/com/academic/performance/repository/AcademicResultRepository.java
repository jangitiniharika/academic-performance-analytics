package com.academic.performance.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.AcademicResult;

/**
 * Database access for {@link AcademicResult}.
 *
 * <p>{@code findByStudentId} gives every graded subject, which the service layer
 * uses to compute CGPA = sum(gradePoints x credits) / sum(credits).
 */
@Repository
public interface AcademicResultRepository extends JpaRepository<AcademicResult, Long> {

    List<AcademicResult> findByStudentId(Long studentId);

    List<AcademicResult> findByStudentIdAndSemester(Long studentId, Integer semester);

    List<AcademicResult> findByStudentIdOrderBySemesterDesc(Long studentId);

    long countByStudentId(Long studentId);

    boolean existsByStudentIdAndSubjectIdAndSemester(Long studentId, Long subjectId, Integer semester);

    /** Paginated, filtered listing; a {@code null} filter is ignored. */
    @Query("""
            select r from AcademicResult r
            where (:studentId is null or r.student.id = :studentId)
              and (:semester is null or r.semester = :semester)
            """)
    Page<AcademicResult> search(@Param("studentId") Long studentId,
                                @Param("semester") Integer semester,
                                Pageable pageable);
}

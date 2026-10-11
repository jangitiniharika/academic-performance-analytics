package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Marks;

/** Database access for {@link Marks}. */
@Repository
public interface MarksRepository extends JpaRepository<Marks, Long> {

    List<Marks> findByStudentId(Long studentId);

    List<Marks> findByExamId(Long examId);

    Optional<Marks> findByStudentIdAndExamId(Long studentId, Long examId);

    boolean existsByStudentIdAndExamId(Long studentId, Long examId);

    /** All marks for one student in one subject (walks student -> exam -> subject). */
    List<Marks> findByStudentIdAndExam_SubjectId(Long studentId, Long subjectId);

    /** All marks recorded for any exam of a subject (class analytics). */
    List<Marks> findByExam_SubjectId(Long subjectId);

    long countByStudentId(Long studentId);

    /** Paginated, filtered listing; a {@code null} filter is ignored. */
    @Query("""
            select m from Marks m
            where (:studentId is null or m.student.id = :studentId)
              and (:examId is null or m.exam.id = :examId)
              and (:subjectId is null or m.exam.subject.id = :subjectId)
            """)
    Page<Marks> search(@Param("studentId") Long studentId,
                       @Param("examId") Long examId,
                       @Param("subjectId") Long subjectId,
                       Pageable pageable);
}

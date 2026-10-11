package com.academic.performance.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Exam;

/** Database access for {@link Exam}. */
@Repository
public interface ExamRepository extends JpaRepository<Exam, Long> {

    List<Exam> findBySubjectId(Long subjectId);

    List<Exam> findBySubjectIdOrderByExamDateDesc(Long subjectId);

    Page<Exam> findBySubjectId(Long subjectId, Pageable pageable);

    /** Paginated, filtered listing; a {@code null} filter is ignored. */
    @Query("""
            select e from Exam e
            where (:subjectId is null or e.subject.id = :subjectId)
            """)
    Page<Exam> search(@Param("subjectId") Long subjectId, Pageable pageable);
}

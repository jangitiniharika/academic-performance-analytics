package com.academic.performance.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Assignment;

/** Database access for {@link Assignment}. */
@Repository
public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    List<Assignment> findBySubjectId(Long subjectId);

    Page<Assignment> findBySubjectId(Long subjectId, Pageable pageable);

    List<Assignment> findByFacultyId(Long facultyId);

    /** Assignments whose due date has already passed. */
    List<Assignment> findByDueDateBefore(LocalDate date);

    /** Paginated, filtered listing; a {@code null} filter is ignored. */
    @Query("""
            select a from Assignment a
            where (:subjectId is null or a.subject.id = :subjectId)
              and (:facultyId is null or a.faculty.id = :facultyId)
            """)
    Page<Assignment> search(@Param("subjectId") Long subjectId,
                            @Param("facultyId") Long facultyId,
                            Pageable pageable);
}

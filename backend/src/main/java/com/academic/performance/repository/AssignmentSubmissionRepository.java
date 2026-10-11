package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.AssignmentSubmission;
import com.academic.performance.entity.SubmissionStatus;

/** Database access for {@link AssignmentSubmission}. */
@Repository
public interface AssignmentSubmissionRepository extends JpaRepository<AssignmentSubmission, Long> {

    List<AssignmentSubmission> findByAssignmentId(Long assignmentId);

    List<AssignmentSubmission> findByStudentId(Long studentId);

    Optional<AssignmentSubmission> findByAssignmentIdAndStudentId(Long assignmentId, Long studentId);

    long countByAssignmentId(Long assignmentId);

    long countByAssignmentIdAndStatus(Long assignmentId, SubmissionStatus status);

    long countByStudentIdAndStatus(Long studentId, SubmissionStatus status);

    long countByStatus(SubmissionStatus status);

    /** Paginated, filtered listing; a {@code null} filter is ignored. */
    @Query("""
            select s from AssignmentSubmission s
            where (:assignmentId is null or s.assignment.id = :assignmentId)
              and (:studentId is null or s.student.id = :studentId)
            """)
    Page<AssignmentSubmission> search(@Param("assignmentId") Long assignmentId,
                                      @Param("studentId") Long studentId,
                                      Pageable pageable);
}

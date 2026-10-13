package com.academic.performance.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.AlertSeverity;
import com.academic.performance.entity.AlertType;
import com.academic.performance.entity.PerformanceAlert;

/** Database access for {@link PerformanceAlert}. */
@Repository
public interface PerformanceAlertRepository extends JpaRepository<PerformanceAlert, Long> {

    List<PerformanceAlert> findByStudentIdOrderByCreatedAtDesc(Long studentId);

    /** Alerts nobody has acknowledged yet - the faculty "needs attention" list. */
    List<PerformanceAlert> findByAcknowledgedFalseOrderByCreatedAtDesc();

    long countByStudentIdAndAcknowledgedFalse(Long studentId);

    long countByAcknowledgedFalse();

    long countByAcknowledgedFalseAndStudentDepartmentId(Long departmentId);

    List<PerformanceAlert> findByAcknowledgedFalseAndStudentDepartmentIdOrderByCreatedAtDesc(Long departmentId);

    List<PerformanceAlert> findBySeverity(AlertSeverity severity);

    List<PerformanceAlert> findByStudentIdAndAcknowledgedFalseOrderByCreatedAtDesc(Long studentId);

    boolean existsByStudentIdAndTypeAndAcknowledgedFalse(Long studentId, AlertType type);

    /**
     * Paginated, filtered listing; a {@code null} filter is ignored. The
     * {@code departmentId} filter backs the faculty data-scope (a faculty member
     * only sees alerts for students in their department).
     */
    @Query("""
            select a from PerformanceAlert a
            where (:studentId is null or a.student.id = :studentId)
              and (:departmentId is null or a.student.department.id = :departmentId)
              and (:severity is null or a.severity = :severity)
              and (:acknowledged is null or a.acknowledged = :acknowledged)
            order by a.createdAt desc
            """)
    Page<PerformanceAlert> search(@Param("studentId") Long studentId,
                                  @Param("departmentId") Long departmentId,
                                  @Param("severity") AlertSeverity severity,
                                  @Param("acknowledged") Boolean acknowledged,
                                  Pageable pageable);
}

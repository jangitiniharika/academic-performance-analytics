package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Subject;

/** Database access for {@link Subject}. */
@Repository
public interface SubjectRepository extends JpaRepository<Subject, Long> {

    Optional<Subject> findByCode(String code);

    boolean existsByCode(String code);

    List<Subject> findByDepartmentId(Long departmentId);

    List<Subject> findByFacultyId(Long facultyId);

    List<Subject> findBySemester(Integer semester);

    /**
     * Paginated search with optional department and semester filters.
     * Any {@code null} argument is ignored, so one query covers every combination.
     */
    @Query("""
            select s from Subject s
            where (:departmentId is null or s.department.id = :departmentId)
              and (:semester is null or s.semester = :semester)
              and (:search is null
                   or lower(s.name) like lower(concat('%', :search, '%'))
                   or lower(s.code) like lower(concat('%', :search, '%')))
            """)
    Page<Subject> search(@Param("departmentId") Long departmentId,
                         @Param("semester") Integer semester,
                         @Param("search") String search,
                         Pageable pageable);
}

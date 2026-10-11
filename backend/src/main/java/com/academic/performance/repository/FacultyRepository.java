package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Faculty;

/** Database access for {@link Faculty}. */
@Repository
public interface FacultyRepository extends JpaRepository<Faculty, Long> {

    Optional<Faculty> findByEmployeeCode(String employeeCode);

    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);

    /** Finds the faculty record behind a login account. */
    Optional<Faculty> findByUser_Username(String username);

    List<Faculty> findByDepartmentId(Long departmentId);

    /**
     * Paginated search with an optional department filter. The search text matches
     * first name, last name or employee code (case-insensitively); a {@code null}
     * value is ignored.
     */
    @Query("""
            select f from Faculty f
            where (:departmentId is null or f.department.id = :departmentId)
              and (:search is null
                   or lower(f.firstName) like lower(concat('%', :search, '%'))
                   or lower(f.lastName) like lower(concat('%', :search, '%'))
                   or lower(f.employeeCode) like lower(concat('%', :search, '%')))
            """)
    Page<Faculty> search(@Param("departmentId") Long departmentId,
                         @Param("search") String search,
                         Pageable pageable);
}

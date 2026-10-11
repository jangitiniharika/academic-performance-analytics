package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Department;

/** Database access for {@link Department}. */
@Repository
public interface DepartmentRepository extends JpaRepository<Department, Long> {

    Optional<Department> findByCode(String code);

    Optional<Department> findByName(String name);

    boolean existsByCode(String code);

    List<Department> findByNameContainingIgnoreCase(String name);

    /**
     * Paginated search over name or code.
     *
     * <p>A {@code null} (or blank, normalised by the service) search matches every
     * row, so one query supports both "list all" and "search".
     */
    @Query("""
            select d from Department d
            where (:search is null
                   or lower(d.name) like lower(concat('%', :search, '%'))
                   or lower(d.code) like lower(concat('%', :search, '%')))
            """)
    Page<Department> search(@Param("search") String search, Pageable pageable);
}

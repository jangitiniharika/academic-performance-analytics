package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Student;

/** Database access for {@link Student}. */
@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {

    Optional<Student> findByRollNumber(String rollNumber);

    Optional<Student> findByRegistrationNumber(String registrationNumber);

    boolean existsByRollNumber(String rollNumber);

    boolean existsByRegistrationNumber(String registrationNumber);

    boolean existsByEmail(String email);

    /** Finds the student record behind a login account. */
    Optional<Student> findByUser_Username(String username);

    List<Student> findByDepartmentId(Long departmentId);

    Page<Student> findByDepartmentId(Long departmentId, Pageable pageable);

    /** Search by first OR last name, case-insensitively, with pagination. */
    Page<Student> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(
            String firstName, String lastName, Pageable pageable);

    long countByDepartmentId(Long departmentId);

    /**
     * Paginated search with an optional department filter. The search text matches
     * first name, last name or roll number (case-insensitively); a {@code null}
     * value is ignored.
     */
    @Query("""
            select s from Student s
            where (:departmentId is null or s.department.id = :departmentId)
              and (:search is null
                   or lower(s.firstName) like lower(concat('%', :search, '%'))
                   or lower(s.lastName) like lower(concat('%', :search, '%'))
                   or lower(s.rollNumber) like lower(concat('%', :search, '%')))
            """)
    Page<Student> search(@Param("departmentId") Long departmentId,
                         @Param("search") String search,
                         Pageable pageable);
}

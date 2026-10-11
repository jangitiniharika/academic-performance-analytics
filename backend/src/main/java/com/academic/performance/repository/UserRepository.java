package com.academic.performance.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.academic.performance.entity.Role;
import com.academic.performance.entity.User;

/**
 * Database access for {@link User}.
 *
 * <p>Repository interfaces contain NO business logic - they only declare the
 * queries the application needs. Spring Data JPA writes the implementation.
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    /** Used during registration to reject duplicate usernames. */
    boolean existsByUsername(String username);

    /** Used during registration to reject duplicate emails. */
    boolean existsByEmail(String email);

    List<User> findByRole(Role role);
}

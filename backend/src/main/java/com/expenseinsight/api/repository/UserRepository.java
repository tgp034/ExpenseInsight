package com.expenseinsight.api.repository;

import com.expenseinsight.api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for {@link User} entities.
 *
 * <p>Provides basic lookup operations used by authentication and user management
 * flows. Backed by Spring Data JPA.</p>
 *
 * @author ExpenseInsight Team
 * @version 1.0
 * @since 1.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    /**
     * Find a user by their email address.
     *
     * @param email the email address to search for
     * @return an Optional containing the User when found, or empty otherwise
     */
    Optional<User> findByEmail(String email);

    /**
     * Checks whether a user exists with the given email.
     *
     * @param email the email address to check
     * @return true if a user with the email exists
     */
    boolean existsByEmail(String email);

    /**
     * Finds an active user (isActive = true) by email.
     *
     * @param email the email address to search for
     * @return an Optional containing the active User when found
     */
    Optional<User> findByEmailAndIsActiveTrue(String email);
}
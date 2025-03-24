package com.makibeans.repository;

import com.makibeans.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for managing User entities.
 * Provides methods to perform CRUD operations and custom queries on User data.
 */

@Repository
public interface UserRepository extends JpaRepository<User, Long> {


    /**
     * Finds a User by their username.
     *
     * @param username the username of the User to find.
     * @return an Optional containing the User if found, or empty if not found.
     */

    Optional<User> findByUsername(String username);

    /**
     * Checks if a User with the given username exists.
     *
     * @param username the username to check for existence.
     * @return true if a User with the given username exists, false otherwise.
     */

    boolean existsByUsername(String username);

    /**
     * Checks if a User with the given email exists.
     *
     * @param email the email to check for existence.
     * @return true if a User with the given email exists, false otherwise.
     */

    boolean existsByEmail(String email);
}

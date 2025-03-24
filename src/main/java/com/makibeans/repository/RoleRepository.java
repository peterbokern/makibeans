package com.makibeans.repository;

import com.makibeans.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

/**
 * Repository interface for managing Role entities.
 * Provides methods to perform CRUD operations and custom queries on Role data.
 */

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    /**
     * Finds a Role by its name.
     *
     * @param name the name of the role to find.
     * @return an Optional containing the Role if found, or empty if not found.
     */

    Optional<Role> findByName(String name);

    /**
     * Checks if a Role with the given name exists.
     *
     * @param name the name to check for existence.
     * @return true if a Role with the given name exists, false otherwise.
     */

    boolean existsByName(String name);
}

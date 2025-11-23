package com.makibeans.service.impl;

import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.model.Role;
import com.makibeans.repository.RoleRepository;
import com.makibeans.service.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Roles.
 * Provides methods to retrieve and check existence of roles by name.
 */

@Service
public class RoleServiceImpl implements RoleService {

    private final RoleRepository repo;

    @Autowired
    public RoleServiceImpl(RoleRepository repo) {
        this.repo = repo;
    }


    /**
     * Checks if a Role with the given name exists.
     *
     * @param name the name of the role to check.
     * @return true if a role with the given name exists, false otherwise.
     */

    @Override
    public boolean existsByName(String name) {
        return repo.existsByName(name);
    }

    /**
     * Creates a new Role with the given name.
     *
     * @param name the name of the role to create.
     * @throws DuplicateResourceException if a role with the given name already exists.
     */

    @Override
    @Transactional
    public void createRole(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Role name cannot be blank.");
        }
        String normalizedRoleName = name.trim();
        validateUniqueRoleName(normalizedRoleName);
        Role role = new Role(normalizedRoleName);
        repo.save(role);
    }

    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return repo.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role with name " + name + " not found."));
    }

    /**
     * Validates the uniqueness of a Role based on the given name.
     * Throws a DuplicateResourceException if a Role with the same name already exists.
     *
     * @param name the name of the Role to check
     * @throws DuplicateResourceException if a Role with the same name already exists
     */

    private void validateUniqueRoleName(String name) {
        if (repo.existsByName(name)) {
            throw new DuplicateResourceException("Role with name " + name + " already exists.");
        }
    }

}

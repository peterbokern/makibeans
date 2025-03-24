package com.makibeans.service;

import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.model.Role;
import com.makibeans.repository.RoleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for managing Roles.
 * Provides methods to retrieve and check existence of roles by name.
 */

@Service
public class RoleService extends AbstractCrudService<Role, Long> {

    private final RoleRepository roleRepository;

    @Autowired
    public RoleService(RoleRepository roleRepository) {
        super(roleRepository);
        this.roleRepository = roleRepository;
    }

    /**
     * Retrieves a Role by its name.
     *
     * @param name the name of the role to retrieve.
     * @return the Role entity.
     * @throws ResourceNotFoundException if the role with the given name does not exist.
     */

    @Transactional(readOnly = true)
    public Role findByName(String name) {
        return roleRepository.findByName(name)
                .orElseThrow(() -> new ResourceNotFoundException("Role with name " + name + " not found."));
    }

    /**
     * Checks if a Role with the given name exists.
     *
     * @param name the name of the role to check.
     * @return true if a role with the given name exists, false otherwise.
     */

    public boolean existsByName(String name) {
        return roleRepository.existsByName(name);
    }

    /**
     * Creates a new Role with the given name.
     *
     * @param name the name of the role to create.
     * @throws DuplicateResourceException if a role with the given name already exists.
     */

    @Transactional
    public void createRole(String name) {

        if (roleRepository.existsByName(name)) {
            throw new DuplicateResourceException("Role with name " + name + " already exists.");
        }

        Role role = new Role();
        role.setName(name);
        create(role);
    }
}

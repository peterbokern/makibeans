package com.makibeans.config;

import com.makibeans.dto.UserRequestDTO;
import com.makibeans.service.RoleService;
import com.makibeans.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * Component for initializing data in the database.
 * Implements CommandLineRunner to execute code after the application starts.
 */

@Component
public class DataInitializer implements CommandLineRunner {

    private final RoleService roleService;
    private final UserService userService;

    public DataInitializer(RoleService roleService, UserService userService) {
        this.roleService = roleService;
        this.userService = userService;
    }

    /**
     * Runs the data initialization process.
     *
     * @param args command line arguments
     * @throws Exception if an error occurs during initialization
     */

    @Override
    public void run(String... args) throws Exception {
        initRoles();
    }

    /**
     * Initializes the roles in the database.
     */

    private void initRoles() {
        createRoleIfNotExists("ROLE_USER");
        createRoleIfNotExists("ROLE_ADMIN");
        createAdminIfNotExists();
    }

    /**
     * Creates a role if it does not already exist.
     *
     * @param name the name of the role
     */

    private void createRoleIfNotExists(String name) {
        if (!roleService.existsByName(name)) {
            roleService.createRole(name);
        }
    }

    /**
     * Creates an admin user if it does not already exist.
     */

    private void createAdminIfNotExists() {
        if (!userService.existsByUsername("maki_admin")) {
            userService.registerAdmin(new UserRequestDTO("maki_admin", "maki_admin@makibeans.com", "maki_admin"));
        }
    }
}

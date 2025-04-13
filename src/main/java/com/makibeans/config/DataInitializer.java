package com.makibeans.config;

import com.makibeans.dto.user.UserRequestDTO;
import com.makibeans.service.RoleService;
import com.makibeans.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

/**
 * DataInitializer is a Spring Boot component that initializes roles and an admin user
 * when the application starts.
 */

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;
    private final RoleService roleService;

    public DataInitializer(UserService userService, RoleService roleService, RoleService roleService1) {
        this.userService = userService;
        this.roleService = roleService1;
    }

    @Override
    public void run(String... args) {
        initRoles();
        initAdmin();
        initUser();
    }

    /**
     * Initializes the default roles for the application.
     */

    private void initRoles() {
        roleService.createRole("ROLE_USER");
        roleService.createRole("ROLE_ADMIN");
    }

    /**
     * Initializes the admin user if it does not already exist.
     */

    private void initAdmin() {
        if (!userService.existsByUsername("maki_admin")) {
            UserRequestDTO admin = new UserRequestDTO("maki_admin", "maki_admin@makibeans.nl", "maki_admin");
            userService.registerAdmin(admin);
        }
    }

    /**
     * Initializes a regular user if it does not already exist.
     */

    private void initUser() {
        if (!userService.existsByUsername("regular_user")) {
            UserRequestDTO user = new UserRequestDTO("regular_user", "regular_user@example.nl", "regular_user");
            userService.registerUser(user);
        }
    }
}


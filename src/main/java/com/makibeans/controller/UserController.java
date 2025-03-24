package com.makibeans.controller;

import com.makibeans.dto.UserRequestDTO;
import com.makibeans.dto.UserResponseDTO;
import com.makibeans.dto.UserUpdateDTO;
import com.makibeans.model.User;
import com.makibeans.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST controller for managing users.
 * Provides endpoints for user registration, retrieval, update, and deletion.
 */

@RestController
@RequestMapping("/users")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their ID (Admin only).
     */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves the currently authenticated user's profile.
     */

    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    /**
     * Retrieves all users (Admin only).
     */

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        return ResponseEntity.ok(userService.getAllUsers());
    }

    /**
     * Registers a new user.
     */

    @PostMapping
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(userRequestDTO));
    }

    /**
     * Registers a new admin user (Admin only).
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<UserResponseDTO> registerAdmin(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerAdmin(userRequestDTO));
    }

    /**
     * Deletes the authenticated user's own account.
     */

    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes any user (Admin only).
     */

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the authenticated user's own account.
     */

    @PreAuthorize("isAuthenticated()")
    @PutMapping("/me")
    public ResponseEntity<UserResponseDTO> updateOwnUser(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUser(user.getId(), userUpdateDTO));
    }

    /**
     * Updates any user (Admin only).
     */

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateAnyUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO));
    }
}

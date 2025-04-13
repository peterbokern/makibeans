package com.makibeans.controller;

import com.makibeans.dto.user.UserRequestDTO;
import com.makibeans.dto.user.UserResponseDTO;
import com.makibeans.dto.user.UserUpdateDTO;
import com.makibeans.model.User;
import com.makibeans.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * REST controller for managing users.
 * Provides endpoints for user registration, retrieval, update, and deletion.
 */
@RestController
@RequestMapping("/users")
@Tag(name = "Users", description = "User registration, profile management, and admin control")
public class UserController {

    private final UserService userService;
    public UserController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Retrieves a user by their ID (Admin only).
     */
    @Operation(summary = "Get user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDTO> getUserById(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    /**
     * Retrieves the currently authenticated user's profile.
     */
    @Operation(summary = "Get current authenticated user's profile")
    @PreAuthorize("isAuthenticated()")
    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getUserById(user.getId()));
    }

    /**
     * Retrieves all Sizes, or filters them based on search parameters. (Admin Only)
     *
     * @param params optional search, sort, and order parameters
     * @return a ResponseEntity containing a list of SizeResponseDTOs
     */
    @Operation(
            summary = "Get all users or search by filters (Admin only)",
            description = "Fetch users with optional filtering and sorting. " +
                    "Parameters include:\n" +
                    "- `search`: Partial match on username or email.\n" +
                    "- `username`: Exact match on username.\n" +
                    "- `email`: Exact match on email.\n" +
                    "- `sort`: Field to sort by (`id`, `username`, `email`).\n" +
                    "- `order`: Sort order (`asc`, `desc`).")
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public ResponseEntity<List<UserResponseDTO>> getUsers(@RequestParam Map<String, String> params) {
        List<UserResponseDTO> userResponseDTOS = userService.findBySearchQuery(params);
        return ResponseEntity.ok(userResponseDTOS);
    }

    /**
     * Registers a new user.
     */
    @Operation(summary = "Register a new user")
    @PostMapping("/register")
    public ResponseEntity<UserResponseDTO> registerUser(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerUser(userRequestDTO));
    }

    /**
     * Registers a new admin user (Admin only).
     */
    @Operation(summary = "Register a new admin (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/admin")
    public ResponseEntity<UserResponseDTO> registerAdmin(@Valid @RequestBody UserRequestDTO userRequestDTO) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(userService.registerAdmin(userRequestDTO));
    }

    /**
     * Deletes the authenticated user's own account.
     */
    @Operation(summary = "Delete own user account")
    @PreAuthorize("isAuthenticated()")
    @DeleteMapping("/me")
    public ResponseEntity<Void> deleteOwnAccount(@AuthenticationPrincipal User user) {
        userService.deleteUser(user.getId());
        return ResponseEntity.noContent().build();
    }

    /**
     * Deletes any user (Admin only).
     */
    @Operation(summary = "Delete any user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserById(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Updates the authenticated user's own account.
     */
    @Operation(summary = "Update own user account")
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
    @Operation(summary = "Update any user by ID (Admin only)")
    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<UserResponseDTO> updateAnyUser(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO userUpdateDTO) {
        return ResponseEntity.ok(userService.updateUser(id, userUpdateDTO));
    }
}

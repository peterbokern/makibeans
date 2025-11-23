package com.makibeans.controller;

import com.makibeans.dto.user.UserRequestDTO;
import com.makibeans.dto.user.UserResponseDTO;
import com.makibeans.dto.user.UserUpdateDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.UserFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.service.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Manage users")
public class UserController {

    private final UserService service;

    // Inline DTOs for specific actions
    @Data
    public static class PasswordSetRequest {
        @NotBlank private String password;
    }

    @Data
    public static class PasswordChangeRequest {
        @NotBlank private String currentPassword;
        @NotBlank private String newPassword;
    }

    @Data
    public static class PasswordResetRequest {
        @Email @NotBlank private String email;
    }

    @Data
    public static class PasswordResetConfirmRequest {
        @NotBlank private String token;
        @NotBlank private String newPassword;
    }



    // ---------- READS ----------

    @GetMapping("/{id}")
    @Operation(summary = "Get user by id")
    public ResponseEntity<UserResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    @GetMapping
    @Operation(summary = "Get users (paged)", description = "Search/sort/paginate users using query params.")
    public ResponseEntity<Page<UserResponseDTO>> getAll(
            @Valid @ModelAttribute UserFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<UserFilter> req = SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);
        return ResponseEntity.ok(service.search(req));
    }

    @PostMapping("/search")
    @Operation(summary = "Search users (POST)", description = "Same as GET but accepts a JSON body for complex filters.")
    public ResponseEntity<Page<UserResponseDTO>> search(
            @Valid @RequestBody SearchRequest<UserFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<UserFilter> merged = SearchRequestUtils.mergeWithPageable(request, pageable);
        return ResponseEntity.ok(service.search(merged));
    }

    // ---------- REGISTRATION ----------

    @PostMapping("/register")
    @Operation(summary = "Register a normal user")
    public ResponseEntity<UserResponseDTO> register(@Valid @RequestBody UserRequestDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerUser(body));
    }

    @PostMapping("/register/admin")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Register an admin user")
    public ResponseEntity<UserResponseDTO> registerAdmin(@Valid @RequestBody UserRequestDTO body) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerAdmin(body));
    }

    // ---------- WRITES (ADMIN) ----------

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Create user (admin)")
    public ResponseEntity<UserResponseDTO> create(@Valid @RequestBody UserRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerUser(dto));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Update user (admin)")
    public ResponseEntity<UserResponseDTO> update(@PathVariable Long id, @Valid @RequestBody UserUpdateDTO body) {
        return ResponseEntity.ok(service.update(id, body));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Delete user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    //TODO: Add endpoints for password management (set, change, reset)
}

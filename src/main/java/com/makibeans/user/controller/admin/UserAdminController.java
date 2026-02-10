package com.makibeans.user.controller.admin;

import com.makibeans.user.dto.*;
import com.makibeans.user.mapper.UserMapper;
import com.makibeans.user.model.User;
import com.makibeans.search.SearchRequest;
import com.makibeans.user.filter.UserFilter;
import com.makibeans.search.utils.SearchRequestUtils;
import com.makibeans.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/users")
@RequiredArgsConstructor
@Tag(name = "Users (Admin)", description = "Admin operations for managing users")
@PreAuthorize("hasRole('ADMIN')")
public class UserAdminController {

    private final UserService userService;
    private final UserMapper userMapper;

    // -------------------------------------------------------------------------
    // READ
    // -------------------------------------------------------------------------
    @GetMapping("/{id}")
    @Operation(summary = "Admin: Get user by id")
    public ResponseEntity<UserAdminResponseDTO> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return ResponseEntity.ok(userMapper.toAdminResponseDTO(user));
    }

    @GetMapping
    @Operation(
            summary = "Admin: Get users (paged)",
            description = "Search/sort/paginate users using query parameters."
    )
    public ResponseEntity<Page<UserAdminResponseDTO>> getAll(
            @Valid @ModelAttribute UserFilter filters,
            @RequestParam(required = false) String search,
            @RequestParam(required = false, defaultValue = "false") Boolean includeDeleted,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<UserFilter> req =
                SearchRequestUtils.assemble(filters, search, includeDeleted, pageable);

        Page<User> page = userService.search(req);
        Page<UserAdminResponseDTO> result = page.map(userMapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    @PostMapping("/search")
    @Operation(
            summary = "Admin: Search users (POST)",
            description = "Same as GET but accepts a JSON body for complex filters."
    )
    public ResponseEntity<Page<UserAdminResponseDTO>> search(
            @Valid @RequestBody SearchRequest<UserFilter> request,
            @PageableDefault(size = 20, sort = "id") Pageable pageable
    ) {
        SearchRequest<UserFilter> merged =
                SearchRequestUtils.mergeWithPageable(request, pageable);

        Page<User> page = userService.search(merged);
        Page<UserAdminResponseDTO> result = page.map(userMapper::toAdminResponseDTO);

        return ResponseEntity.ok(result);
    }

    // -------------------------------------------------------------------------
    // REGISTRATION (Admin-created users)
    // -------------------------------------------------------------------------
    @PostMapping("/register")
    @Operation(summary = "Admin: Register a normal user")
    public ResponseEntity<UserAdminResponseDTO> registerUser(
            @Valid @RequestBody UserRequestDTO body
    ) {
        User created = userService.registerUser(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toAdminResponseDTO(created));
    }

    @PostMapping("/register/admin")
    @Operation(summary = "Admin: Register an admin user")
    public ResponseEntity<UserAdminResponseDTO> registerAdmin(
            @Valid @RequestBody UserRequestDTO body
    ) {
        User created = userService.registerAdmin(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toAdminResponseDTO(created));
    }

    // -------------------------------------------------------------------------
    // UPDATE / DELETE
    // -------------------------------------------------------------------------
    @PutMapping("/{id}")
    @Operation(summary = "Admin: Update user")
    public ResponseEntity<UserAdminResponseDTO> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpdateDTO body
    ) {
        User updated = userService.update(id, body);
        return ResponseEntity.ok(userMapper.toAdminResponseDTO(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin: Delete user")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        userService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // -------------------------------------------------------------------------
    // PASSWORD MANAGEMENT (still conceptually "unfinished")
    // -------------------------------------------------------------------------

    @PostMapping("/{id}/password/set")
    @Operation(summary = "Admin: Set password for user")
    public ResponseEntity<Void> setPassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordSetRequestDTO body
    ) {
        userService.setPassword(id, body);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/password/change")
    @Operation(summary = "Admin: Change password for user")
    public ResponseEntity<Void> changePassword(
            @PathVariable Long id,
            @Valid @RequestBody PasswordChangeRequestDTO body
    ) {
        userService.changePassword(id, body);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/password/reset")
    @Operation(summary = "Admin: Trigger password reset for user (request)")
    public ResponseEntity<Void> resetPasswordRequest(
            @Valid @RequestBody PasswordResetRequestDTO body
    ) {
        userService.resetPasswordRequest(body);
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/password/reset/confirm")
    @Operation(summary = "Admin: Confirm password reset")
    public ResponseEntity<Void> resetPasswordConfirm(
            @Valid @RequestBody PasswordResetConfirmRequestDTO body
    ) {
        userService.resetPasswordConfirm(body);
        return ResponseEntity.noContent().build();
    }
}

package com.makibeans.user.controller.pub;

import com.makibeans.user.dto.UserPublicResponseDTO;
import com.makibeans.user.dto.UserRequestDTO;
import com.makibeans.user.mapper.UserMapper;
import com.makibeans.user.model.User;
import com.makibeans.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "Public user operations (registration, profile)")
public class UserPublicController {

    private final UserService userService;
    private final UserMapper userMapper;

    // -------------------------------------------------------------------------
    // Registration (normal user)
    // -------------------------------------------------------------------------
    @PostMapping("/register")
    @Operation(summary = "Register a normal user")
    public ResponseEntity<UserPublicResponseDTO> register(
            @Valid @RequestBody UserRequestDTO body
    ) {
        User created = userService.registerUser(body);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(userMapper.toPublicResponseDTO(created));
    }

    // -------------------------------------------------------------------------
    // Current user profile (/me)
    // -------------------------------------------------------------------------
    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Get current authenticated user")
    public ResponseEntity<UserPublicResponseDTO> getCurrentUser(Authentication authentication) {
        String username = authentication.getName();
        User user = userService.findByUserName(username);
        return ResponseEntity.ok(userMapper.toPublicResponseDTO(user));
    }
}

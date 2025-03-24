package com.makibeans.controller;

import com.makibeans.dto.LoginRequestDTO;
import com.makibeans.dto.LoginResponseDTO;
import com.makibeans.service.AuthService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Controller responsible for user authentication.
 * Handles login requests and returns JWT tokens on success.
 */

@RestController
@RequestMapping("/auth")
public class AuthController {

    private static final Logger logger = LoggerFactory.getLogger(AuthService.class);

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    /**
     * Authenticates a user and returns a JWT token if the credentials are valid.
     *
     * @param loginRequestDTO the login request containing the username and password
     * @return a ResponseEntity containing the LoginResponseDTO with the username, email, and JWT token
     */

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> loginUser(@Valid @RequestBody LoginRequestDTO loginRequestDTO) {
        logger.info("Login attempt for username: {}", loginRequestDTO.getUsername());
        LoginResponseDTO loginResponseDTO = authService.loginUser(loginRequestDTO);
        logger.info("Login succesfull for username: {}", loginRequestDTO.getUsername());
        return ResponseEntity.ok(loginResponseDTO);
    }
}

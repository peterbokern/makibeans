package com.makibeans.service;

import com.makibeans.dto.LoginRequestDTO;
import com.makibeans.dto.LoginResponseDTO;
import com.makibeans.exceptions.InvalidCredentialsException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.model.User;
import com.makibeans.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UserService userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    /**
     * Authenticates a user and generates a JWT token if the credentials are valid.
     *
     * @param loginRequestDTO the login request containing the username and password
     * @return a LoginResponseDTO containing the username, email, and JWT token
     * @throws InvalidCredentialsException if the username or password is incorrect
     */

    @Transactional
    public LoginResponseDTO loginUser(@Valid LoginRequestDTO loginRequestDTO) {
        User user;

        try {
            user = userService.findByUserName(loginRequestDTO.getUsername());
        } catch (ResourceNotFoundException ex) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        if (!passwordEncoder.matches(loginRequestDTO.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Invalid username or password.");
        }

        String token = jwtUtil.generateToken(user);

        return new LoginResponseDTO(
                user.getUsername(),
                user.getEmail(),
                token);
    }
}

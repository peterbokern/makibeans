package com.makibeans.authentication.service;

import com.makibeans.authentication.dto.LoginRequestDTO;
import com.makibeans.authentication.dto.LoginResponseDTO;
import com.makibeans.web.exceptions.InvalidCredentialsException;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import com.makibeans.authentication.mapper.AuthMapper;
import com.makibeans.user.service.UserServiceImpl;
import com.makibeans.user.model.User;
import com.makibeans.security.JwtUtil;
import jakarta.validation.Valid;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service class for handling user authentication.
 */

@Service
public class AuthServiceImpl implements AuthService {

    private final UserServiceImpl userService;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthMapper authMapper;


    public AuthServiceImpl(UserServiceImpl userService, PasswordEncoder passwordEncoder, JwtUtil jwtUtil, AuthMapper authMapper) {
        this.userService = userService;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.authMapper = authMapper;
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

        return authMapper.toResponseDTO(user, token);
    }

}

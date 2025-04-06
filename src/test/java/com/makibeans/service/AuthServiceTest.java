package com.makibeans.service;

import static org.junit.jupiter.api.Assertions.*;

import com.makibeans.dto.login.LoginRequestDTO;
import com.makibeans.dto.login.LoginResponseDTO;
import com.makibeans.exceptions.InvalidCredentialsException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.AuthMapper;
import com.makibeans.model.User;
import com.makibeans.security.JwtUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock UserService userService;
    @Mock PasswordEncoder passwordEncoder;
    @Mock JwtUtil jwtUtil;
    @Mock AuthMapper authMapper;

    @InjectMocks AuthService authService;

    User user;
    LoginRequestDTO loginRequestDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("maki");
        user.setEmail("maki@makibeans.com");
        user.setPassword("hashedPassword");

        loginRequestDTO = new LoginRequestDTO("maki", "password123");
    }

    @AfterEach
    void tearDown() {
        user = null;
        loginRequestDTO = null;
    }

    // ========================================
    // LOGIN
    // ========================================

    @Test
    void should_LoginUser_When_CredentialsAreValid() {
        // Arrange
        String token = "jwt-token";
        LoginResponseDTO responseDTO = new LoginResponseDTO("maki", "maki@makibeans.com", token);

        when(userService.findByUserName("maki")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(true);
        when(jwtUtil.generateToken(user)).thenReturn(token);
        when(authMapper.toResponseDTO(user, token)).thenReturn(responseDTO);

        // Act
        LoginResponseDTO result = authService.loginUser(loginRequestDTO);

        // Assert
        assertNotNull(result, "Expected result to be not null");
        assertEquals("maki", result.getUsername(), "Expected username to be 'maki'");
        assertEquals("maki@makibeans.com", result.getEmail(), "Expected email to be 'maki@makibeans.com'");
        assertEquals(token, result.getToken(), "Expected token to match generated token");

        // Verify
        verify(userService).findByUserName("maki");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verify(jwtUtil).generateToken(user);
        verify(authMapper).toResponseDTO(user, token);
        verifyNoMoreInteractions(userService, passwordEncoder, jwtUtil, authMapper);
    }

    @Test
    void should_ThrowInvalidCredentialsException_When_UsernameIsInvalid() {
        // Arrange
        when(userService.findByUserName("maki")).thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.loginUser(loginRequestDTO),
                "Expected InvalidCredentialsException when user not found");

        // Verify
        verify(userService).findByUserName("maki");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void should_ThrowInvalidCredentialsException_When_PasswordIsIncorrect() {
        // Arrange
        when(userService.findByUserName("maki")).thenReturn(user);
        when(passwordEncoder.matches("password123", "hashedPassword")).thenReturn(false);

        // Act & Assert
        assertThrows(
                InvalidCredentialsException.class,
                () -> authService.loginUser(loginRequestDTO),
                "Expected InvalidCredentialsException when password does not match");

        // Verify
        verify(userService).findByUserName("maki");
        verify(passwordEncoder).matches("password123", "hashedPassword");
        verifyNoMoreInteractions(userService, passwordEncoder);
    }
}

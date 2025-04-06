package com.makibeans.service;

import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.model.Role;
import com.makibeans.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@org.junit.jupiter.api.extension.ExtendWith(MockitoExtension.class)
class UserDetailsServiceImplTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserDetailsServiceImpl userDetailsService;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("maki");
        user.setEmail("maki@makibeans.com");
        user.setPassword("hashedpassword");
        user.setRoles(Set.of(new Role("ROLE_USER")));
    }

    @Test
    void should_ReturnUserDetails_When_UserExists() {
        // Arrange
        when(userService.findByUserName("maki")).thenReturn(user);

        // Act
        UserDetails result = userDetailsService.loadUserByUsername("maki");

        // Assert
        assertNotNull(result, "UserDetails should not be null");
        assertEquals("maki", result.getUsername(), "Username should match");
        assertEquals("hashedpassword", result.getPassword(), "Password should match");
        assertFalse(result.getAuthorities().isEmpty(), "Authorities should not be empty");

        // Verify
        verify(userService).findByUserName("maki");
        verifyNoMoreInteractions(userService);
    }

    @Test
    void should_ThrowUsernameNotFoundException_When_UserDoesNotExist() {
        // Arrange
        when(userService.findByUserName("maki")).thenThrow(new ResourceNotFoundException("User not found"));

        // Act & Assert
        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class,
                () -> userDetailsService.loadUserByUsername("maki"),
                "Should throw UsernameNotFoundException when user is not found");

        assertEquals("User not found", exception.getMessage(), "Exception message should match");

        // Verify
        verify(userService).findByUserName("maki");
        verifyNoMoreInteractions(userService);
    }
}

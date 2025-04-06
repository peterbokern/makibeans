package com.makibeans.service;

import com.makibeans.dto.*;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.UserMapper;
import com.makibeans.model.Role;
import com.makibeans.model.User;
import com.makibeans.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock UserRepository userRepository;
    @Mock UserMapper userMapper;
    @Mock PasswordEncoder passwordEncoder;
    @Mock RoleService roleService;

    @InjectMocks UserService userService;

    User user;
    UserRequestDTO requestDTO;
    UserUpdateDTO updateDTO;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("maki");
        user.setEmail("maki@makibeans.com");
        user.setPassword("hashed");
        user.setId(1L);

        requestDTO = new UserRequestDTO("maki", "maki@makibeans.com", "password123");
        updateDTO = new UserUpdateDTO("maki2", "maki2@makibeans.com", "newpass456");
    }

    // ========================================
    // REGISTER
    // ========================================

    @Test
    void should_RegisterUser_When_ValidInput() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("maki2", "maki2@makibeans.com", "password123");
        Role role = new Role("ROLE_USER");
        User user = new User();
        user.setUsername("maki2");
        user.setEmail("maki2@makibeans.com");
        user.setPassword("encodedPassword123");

        when(userMapper.toEntity(dto)).thenReturn(user);
        when(userRepository.existsByUsername("maki2")).thenReturn(false);
        when(userRepository.existsByEmail("maki2@makibeans.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedPassword123");
        when(roleService.findByName("ROLE_USER")).thenReturn(role);
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO response = userService.registerUser(dto);

        // Assert
        assertNotNull(response, "Expected non-null UserResponseDTO on successful registration");
        assertEquals("maki2", user.getUsername(), "Expected username to match the DTO value");
        assertEquals("maki2@makibeans.com", user.getEmail(), "Expected email to match the DTO value");
        assertEquals("encodedPassword123", user.getPassword(), "Expected password to be encoded correctly");

        // Verify
        verify(userMapper).toEntity(any(UserRequestDTO.class));
        verify(userRepository).existsByUsername(anyString());
        verify(userRepository).existsByEmail(anyString());
        verify(passwordEncoder).encode(anyString());
        verify(roleService).findByName("ROLE_USER");
        verify(userMapper).toResponseDTO(any(User.class));
        verify(userRepository).save(any());
        verifyNoMoreInteractions(userMapper, userRepository, passwordEncoder, roleService);
    }

    @Test
    void should_RegisterAdmin_When_ValidInput() {
        // Arrange
        UserRequestDTO dto = new UserRequestDTO("admin", "admin@makibeans.com", "adminpass");
        Role role = new Role("ROLE_ADMIN");
        User adminUser = new User();
        adminUser.setUsername("admin");
        adminUser.setEmail("admin@makibeans.com");
        adminUser.setPassword("encodedAdminPass");

        when(userMapper.toEntity(dto)).thenReturn(adminUser);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        when(userRepository.existsByEmail("admin@makibeans.com")).thenReturn(false);
        when(passwordEncoder.encode(anyString())).thenReturn("encodedAdminPass");
        when(roleService.findByName("ROLE_ADMIN")).thenReturn(role);
        when(userRepository.save(any())).thenReturn(adminUser);
        when(userMapper.toResponseDTO(adminUser)).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO response = userService.registerAdmin(dto);

        // Assert
        assertNotNull(response, "Expected non-null UserResponseDTO for admin registration");
        assertEquals("admin", adminUser.getUsername(), "Expected username to be 'admin'");
        assertEquals("admin@makibeans.com", adminUser.getEmail(), "Expected email to match");
        assertEquals("encodedAdminPass", adminUser.getPassword(), "Expected password to be encoded correctly");
        assertEquals(role, adminUser.getRoles().iterator().next(), "Expected role to be ROLE_ADMIN");

        // Verify
        verify(userMapper).toEntity(dto);
        verify(userRepository).existsByUsername("admin");
        verify(userRepository).existsByEmail("admin@makibeans.com");
        verify(passwordEncoder).encode("encodedAdminPass");
        verify(roleService).findByName("ROLE_ADMIN");
        verify(userRepository).save(adminUser);
        verify(userMapper).toResponseDTO(adminUser);
        verifyNoMoreInteractions(userMapper, userRepository, passwordEncoder, roleService);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_RegisteringWithExistingUsername() {
        // Arrange
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.existsByUsername("maki")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(requestDTO),
                "Expected DuplicateResourceException when registering with existing username");

        // Verify
        verify(userMapper).toEntity(requestDTO);
        verify(userRepository).existsByUsername("maki");
        verifyNoMoreInteractions(userMapper, userRepository);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_RegisteringWithExistingEmail() {
        // Arrange
        when(userMapper.toEntity(requestDTO)).thenReturn(user);
        when(userRepository.existsByUsername("maki")).thenReturn(false);
        when(userRepository.existsByEmail("maki@makibeans.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class, () -> userService.registerUser(requestDTO),
                "Expected DuplicateResourceException when registering with existing email");

        // Verify
        verify(userMapper).toEntity(requestDTO);
        verify(userRepository).existsByUsername("maki");
        verify(userRepository).existsByEmail("maki@makibeans.com");
        verifyNoMoreInteractions(userMapper, userRepository);
    }

    // ========================================
    // FIND BY USERNAME / ID
    // ========================================

    @Test
    void should_ReturnUser_When_UsernameExists() {
        // Arrange
        when(userRepository.findByUsername("maki")).thenReturn(Optional.of(user));

        // Act
        User result = userService.findByUserName("maki");

        // Assert
        assertEquals(user, result);

        //verify
        verify(userRepository).findByUsername("maki");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UsernameNotFound() {
        // Arrange
        when(userRepository.findByUsername("missing")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.findByUserName("missing"));

        // Verify
        verify(userRepository).findByUsername("missing");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void should_ReturnUserResponseDTO_When_IdExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO result = userService.getUserById(1L);

        // Assert
        assertNotNull(result);

        //verify
        verify(userRepository).findById(1L);
        verify(userMapper).toResponseDTO(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_IdNotFound() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.getUserById(99L));

        //verify
        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
    }

    // ========================================
    // GET ALL
    // ========================================

    @Test
    void should_ReturnAllUsers() {
        // Arrange
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO());

        // Act
        List<UserResponseDTO> result = userService.getAllUsers();

        // Assert
        assertEquals(1, result.size());

        //verify
        verify(userRepository).findAll();
        verify(userMapper).toResponseDTO(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }

    // ========================================
    // EXISTS BY USERNAME
    // ========================================

    @Test
    void should_ReturnTrue_When_UserExistsByUsername() {
        //arrange
        when(userRepository.existsByUsername("maki")).thenReturn(true);

        //act & assert
        assertTrue(userService.existsByUsername("maki"));

        //verify
        verify(userRepository).existsByUsername("maki");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void should_ReturnFalse_When_UserDoesNotExistByUsername() {
        //arrange
        when(userRepository.existsByUsername("maki")).thenReturn(false);

        //act & asser
        assertFalse(userService.existsByUsername("maki"));

        //verify
        verify(userRepository).existsByUsername("maki");
        verifyNoMoreInteractions(userRepository);
    }

    // ========================================
    // DELETE
    // ========================================

    @Test
    void should_DeleteUser_When_IdExists() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));

        // Act
        userService.deleteUser(1L);

        // Verify
        verify(userRepository).delete(user);
        verifyNoMoreInteractions(userRepository);
    }

    // ========================================
    // UPDATE
    // ========================================

    @Test
    void should_UpdateUser_When_ValidInput() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("maki2")).thenReturn(false);
        when(userRepository.existsByEmail("maki2@makibeans.com")).thenReturn(false);
        when(passwordEncoder.matches("newpass456", "hashed")).thenReturn(false);
        when(passwordEncoder.encode("newpass456")).thenReturn("newhash");
        when(userRepository.save(any())).thenReturn(user);
        when(userMapper.toResponseDTO(any())).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO result = userService.updateUser(1L, updateDTO);

        // Assert
        assertNotNull(result);

        //verify
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("maki2");
        verify(userRepository).existsByEmail("maki2@makibeans.com");
        verify(passwordEncoder).matches("newpass456", "hashed");
        verify(passwordEncoder).encode("newpass456");
        verify(userRepository).save(any());
        verify(userMapper).toResponseDTO(any());
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void should_NotUpdateUser_When_NoFieldsChanged() {
        // Arrange
        UserUpdateDTO sameData = new UserUpdateDTO("maki", "maki@makibeans.com", "password123");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password123", "hashed")).thenReturn(true);
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO());

        // Act
        UserResponseDTO result = userService.updateUser(1L, sameData);

        // Assert
        assertNotNull(result);

        // Verify
        verify(userRepository).findById(1L);
        verify(passwordEncoder).matches("password123", "hashed");
        verify(userMapper).toResponseDTO(user);
        verifyNoMoreInteractions(userRepository, passwordEncoder, userMapper);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_UpdatingToExistingUsername() {
        // Arrange
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByUsername("maki2")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> userService.updateUser(1L, updateDTO));

        //Verify
        verify(userRepository).findById(1L);
        verify(userRepository).existsByUsername("maki2");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_UpdatingToExistingEmail() {
        // Arrange
        updateDTO.setUsername(null);
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.existsByEmail("maki2@makibeans.com")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> userService.updateUser(1L, updateDTO));

        //verify
        verify(userRepository).findById(1L);
        verify(userRepository).existsByEmail("maki2@makibeans.com");
        verifyNoMoreInteractions(userRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_UpdatingNonExistentUser() {
        // Arrange
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> userService.updateUser(99L, updateDTO));

        //Verify
        verify(userRepository).findById(99L);
        verifyNoMoreInteractions(userRepository);
    }

    // ========================================
    // FILTER
    // ========================================

    @Test
    void should_FilterUsers_ByUsername() {
        // Arrange
        Map<String, String> params = Map.of("username", "maki");
        when(userRepository.findAll()).thenReturn(List.of(user));
        when(userMapper.toResponseDTO(user)).thenReturn(new UserResponseDTO());

        // Act
        List<UserResponseDTO> result = userService.findBySearchQuery(params);

        // Assert
        assertEquals(1, result.size());

        //Verify
        verify(userRepository).findAll();
        verify(userMapper).toResponseDTO(user);
        verifyNoMoreInteractions(userRepository, userMapper);
    }
}

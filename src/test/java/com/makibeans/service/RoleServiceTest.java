package com.makibeans.service;

import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.model.Role;
import com.makibeans.repository.RoleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for RoleService.
 */

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock RoleRepository roleRepository;
    @InjectMocks RoleService roleService;

    Role role;

    @BeforeEach
    void setUp() {
        role = new Role("ROLE_USER");
        role.setId(1L);
    }

    // ========================================
    // FIND BY NAME
    // ========================================

    @Test
    void should_ReturnRole_When_NameExists() {
        // Arrange
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(role));

        // Act
        Role result = roleService.findByName("ROLE_USER");

        // Assert
        assertEquals(role, result, "Expected to return the correct Role when name exists");

        // Verify
        verify(roleRepository).findByName("ROLE_USER");
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void should_ThrowResourceNotFoundException_When_NameDoesNotExist() {
        // Arrange
        when(roleRepository.findByName("ROLE_ADMIN")).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(ResourceNotFoundException.class,
                () -> roleService.findByName("ROLE_ADMIN"),
                "Expected ResourceNotFoundException when role name does not exist");

        // Verify
        verify(roleRepository).findByName("ROLE_ADMIN");
        verifyNoMoreInteractions(roleRepository);
    }

    // ========================================
    // EXISTS BY NAME
    // ========================================

    @Test
    void should_ReturnTrue_When_RoleExists() {
        // Arrange
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        // Act & Assert
        assertTrue(roleService.existsByName("ROLE_USER"), "Expected existsByName to return true when role exists");

        // Verify
        verify(roleRepository).existsByName("ROLE_USER");
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void should_ReturnFalse_When_RoleDoesNotExist() {
        // Arrange
        when(roleRepository.existsByName("ROLE_UNKNOWN")).thenReturn(false);

        // Act & Assert
        assertFalse(roleService.existsByName("ROLE_UNKNOWN"), "Expected existsByName to return false when role does not exist");

        // Verify
        verify(roleRepository).existsByName("ROLE_UNKNOWN");
        verifyNoMoreInteractions(roleRepository);
    }

    // ========================================
    // CREATE
    // ========================================

    @Test
    void should_CreateRole_When_ValidName() {
        // Arrange
        String roleName = "ROLE_MANAGER";
        when(roleRepository.existsByName("ROLE_MANAGER")).thenReturn(false);

        // Act
        roleService.createRole(roleName);

        // Assert
        ArgumentCaptor<Role> roleCaptor = ArgumentCaptor.forClass(Role.class);
        verify(roleRepository).save(roleCaptor.capture());

        // Assert
        assertEquals(roleName, roleCaptor.getValue().getName(), "Expected the saved role to have the correct name");

        // Verify
        verify(roleRepository).existsByName("ROLE_MANAGER");
        verifyNoMoreInteractions(roleRepository);
    }

    @Test
    void should_ThrowIllegalArgumentException_When_RoleNameIsNull() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> roleService.createRole(null),
                "Expected IllegalArgumentException when role name is null");

        // Verify
        verifyNoInteractions(roleRepository);
    }


    @Test
    void should_ThrowIllegalArgumentException_When_RoleNameIsBlank() {
        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> roleService.createRole("  "),
                "Expected IllegalArgumentException when role name is blank");

        // Verify
        verifyNoInteractions(roleRepository);
    }

    @Test
    void should_ThrowDuplicateResourceException_When_RoleAlreadyExists() {
        // Arrange
        when(roleRepository.existsByName("ROLE_USER")).thenReturn(true);

        // Act & Assert
        assertThrows(DuplicateResourceException.class,
                () -> roleService.createRole("ROLE_USER"),
                "Expected DuplicateResourceException when role already exists");

        // Verify
        verify(roleRepository).existsByName("ROLE_USER");
        verifyNoMoreInteractions(roleRepository);
    }
}

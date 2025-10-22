package com.makibeans.service.service;

import com.makibeans.dto.user.UserRequestDTO;
import com.makibeans.dto.user.UserResponseDTO;
import com.makibeans.dto.user.UserUpdateDTO;
import com.makibeans.model.User;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.UserFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService extends CrudService<User, Long> {

    // Basics
    UserResponseDTO getById(Long id);
    Page<UserResponseDTO> search(SearchRequest<UserFilter> request);
    UserResponseDTO create(@Valid UserRequestDTO dto);
    UserResponseDTO update(Long id, @Valid UserUpdateDTO dto);
    boolean existsByEmail(String email);

    // Registration
    UserResponseDTO registerUser(@Valid UserRequestDTO dto);
    UserResponseDTO registerAdmin(@Valid UserRequestDTO dto);

    // Enable/Disable
    void enable(Long id);
    void disable(Long id);

    // Roles
    UserResponseDTO addRoles(Long userId, List<Long> roleIds);
    UserResponseDTO removeRole(Long userId, Long roleId);

    // Password ops
    void setPassword(Long userId, String rawPassword);     // admin-set
    void changePassword(Long userId, String currentRawPassword, String newRawPassword); // self-change
    void resetPasswordRequest(String email);               // send token
    void resetPasswordConfirm(String token, String newRawPassword);
}

package com.makibeans.service.service;

import com.makibeans.dto.user.*;
import com.makibeans.model.User;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.UserFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface UserService extends CrudService<User, Long> {

    // Basics
    UserResponseDTO getById(Long id);
    Page<UserResponseDTO> search(SearchRequest<UserFilter> request);
    UserResponseDTO update(Long id, @Valid UserUpdateDTO dto);


    // Registration
    UserResponseDTO registerUser(@Valid UserRequestDTO dto);
    UserResponseDTO registerAdmin(@Valid UserRequestDTO dto);

    // Enable/Disable
    void enable(Long id);
    void disable(Long id);

    @Transactional
    void setPassword(Long userId, @Valid PasswordSetRequestDTO dto);

    @Transactional
    void changePassword(Long userId, @Valid PasswordChangeRequestDTO dto);

    void resetPasswordRequest(@Valid PasswordResetRequestDTO dto);

    @Transactional
    void resetPasswordConfirm(@Valid PasswordResetConfirmRequestDTO dto);

    boolean existsByUsername(String username);
}

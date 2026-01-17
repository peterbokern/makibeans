package com.makibeans.user.service;

import com.makibeans.user.dto.*;
import com.makibeans.user.model.User;
import com.makibeans.search.SearchRequest;
import com.makibeans.user.filter.UserFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface UserService {

    // Basics
    User getById(Long id);

    Page<User> search(SearchRequest<UserFilter> request);

    User update(Long id, @Valid UserUpdateDTO dto);

    // Registration
    @Transactional
    User registerUser(@Valid UserRequestDTO dto);

    @Transactional
    User registerAdmin(@Valid UserRequestDTO dto);

    @Transactional
    void enable(Long id);

    @Transactional
    void disable(Long id);

    // Password management (unfinished but wired)
    @Transactional
    void setPassword(Long userId, @Valid PasswordSetRequestDTO dto);

    @Transactional
    void changePassword(Long userId, @Valid PasswordChangeRequestDTO dto);

    void resetPasswordRequest(@Valid PasswordResetRequestDTO dto);

    @Transactional
    void resetPasswordConfirm(@Valid PasswordResetConfirmRequestDTO dto);

    boolean existsByUsername(String username);

    // Already present in impl; optional to expose:
    User findByUserName(String username);

    void delete(Long id);
}

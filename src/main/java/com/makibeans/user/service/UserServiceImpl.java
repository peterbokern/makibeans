package com.makibeans.user.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import com.makibeans.role.service.RoleServiceImpl;
import com.makibeans.user.dto.*;
import com.makibeans.user.mapper.UserMapper;
import com.makibeans.role.model.Role;
import com.makibeans.user.model.User;
import com.makibeans.user.repository.UserRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.user.filter.UserFilter;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository repo;
    private final UserMapper mapper;
    private final RoleServiceImpl roleService;              // use service (not repository) for roles
    private final PasswordEncoder passwordEncoder;      // encode passwords


    /* ------------- Reads ------------- */

    @Override
    public User getById(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("User with id " + id + " not found."));
    }

    @Override
    public Page<User> search(SearchRequest<UserFilter> req) {
        Specification<User> spec =
                SpecificationFactory.fromRequest(req, UserFilter.class);

        Sort sort = new SortResolver(UserFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Specification<User> distinctSpec = (root, query, cb) -> {
            Objects.requireNonNull(query, "CriteriaQuery must not be null");
            query.distinct(true);
            return null;
        };

        Specification<User> finalSpec = (spec == null) ? distinctSpec : spec.and(distinctSpec);

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(finalSpec, pageable);
    }



    /* ------------- Writes ------------- */

    @Override
    @Transactional
    public User update(Long id, @Valid UserUpdateDTO dto) {
        User existing = getById(id);
        mapper.updateEntityFromDTO(dto, existing);
        return existing;
    }



    /* ------------- Registration (via RoleService) ------------- */

    @Override
    @Transactional
    public User registerUser(@Valid UserRequestDTO dto) {
        return registerUserWithRole(dto, "ROLE_USER");
    }

    @Override
    @Transactional
    public User registerAdmin(@Valid UserRequestDTO dto) {
        return registerUserWithRole(dto, "ROLE_ADMIN");
    }

    @Transactional
    public User registerUserWithRole(@Valid UserRequestDTO dto, String roleName) {
        // Manual construction (no mapper.toEntity)
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setEmail(dto.getEmail());

        validateUniqueUsername(user.getUsername());
        validateUniqueEmail(user.getEmail());

        user.setPassword(passwordEncoder.encode(dto.getPassword()));

        Role role = roleService.findByName(roleName);
        user.addRole(role); // if you have a convenience method

        return repo.save(user);
    }

    /* ------------- Enable / Disable ------------- */

    @Override
    @Transactional
    public void enable(Long id) {
        User user = getById(id);
        user.setEnabled(true);
    }

    @Override
    @Transactional
    public void disable(Long id) {
        User user = getById(id);
        user.setEnabled(false);
    }

 /*   *//* ------------- Roles ------------- *//*
    //TODO implement role management if needed in roleservice
    @Override
    @Transactional
    public UserResponseDTO addRoles(Long userId, @Valid RolesRequestDTO dto) {
        User user = UserService.super.getOrThrow(userId);
        for (Long roleId : dto.getRoleIds()) {
            Role r = roleService.getOrThrow(roleId);
            user.getRoles().add(r);
        }
        return mapper.toResponseDTO(user);
    }

    @Override
    @Transactional
    public UserResponseDTO removeRole(Long userId, Long roleId) {
        User user = UserService.super.getOrThrow(userId);
        user.getRoles().removeIf(r -> r.getId().equals(roleId));
        return mapper.toResponseDTO(user);
    }*/

    /* ------------- Passwords ------------- */

    @Override
    @Transactional
    public void setPassword(Long userId, @Valid PasswordSetRequestDTO dto) {
        User user =  getById(userId);
        user.setPassword(encode(dto.getPassword()));
    }

    @Override
    @Transactional
    public void changePassword(Long userId, @Valid PasswordChangeRequestDTO dto) {
        User user = getById(userId);
        if (user.getPassword() == null || !passwordEncoder.matches(dto.getCurrentPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Current password is incorrect");
        }
        user.setPassword(encode(dto.getNewPassword()));
    }


    @Override
    public void resetPasswordRequest(@Valid PasswordResetRequestDTO dto) {
        // TODO: generate token, persist & email it using your services
        // tokenService.create(dto.getEmail()); emailService.sendResetLink(...);
    }


    @Override
    @Transactional
    public void resetPasswordConfirm(@Valid PasswordResetConfirmRequestDTO dto) {
        // TODO: validate/consume token and resolve user, then:
        // User user = tokenService.consume(dto.getToken());
        // user.setPasswordHash(encode(dto.getNewPassword()));
    }

    /* ------------- Helpers ------------- */


    private void validateUniqueUsername(String username) {
        try {
            if (username != null && repo.existsByUsernameIgnoreCase(username)) {
                throw new IllegalArgumentException("Username already in use: " + username);
            }
        } catch (Throwable ignored) { /* username may not exist in your model */ }
    }

    private void validateUniqueEmail(String email) {
        if (email != null && repo.existsByEmailIgnoreCase(email)) {
            throw new IllegalArgumentException("Email already in use: " + email);
        }
    }

    private String encode(String raw) {
        return raw == null ? null : passwordEncoder.encode(raw);
    }

    public User findByUserName(String username) {
        return repo.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException(
                "User with username '" + username + "' not found."));
    }

    @Override
    public void delete(Long id) {
        User user = getById(id);
        user.setDeleted(true);
        user.setDeletedReason(DeleteReason.ADMIN_DELETED);
    }

    @Override
    public boolean existsByUsername(String username) {
        return !repo.existsByUsername(username);
    }
}
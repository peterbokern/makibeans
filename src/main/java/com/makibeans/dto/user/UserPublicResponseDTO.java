package com.makibeans.dto.user;

import java.util.Set;

/**
 * Public view for User (no audit block).
 */
public record UserPublicResponseDTO(
        Long id,
        String username,
        String email,
        Set<String> roles
) {}

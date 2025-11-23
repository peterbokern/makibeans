package com.makibeans.dto.user;

import com.makibeans.dto.audit.AuditableInfo;

import java.util.Set;

/**
 * Admin view for User (includes audit block).
 */
public record UserAdminResponseDTO(
        Long id,
        String username,
        String email,
        Set<String> roles,
        AuditableInfo audit
) {}

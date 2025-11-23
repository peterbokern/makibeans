package com.makibeans.dto.audit;

import java.time.Instant;

/**
 * Small value object to hold audit metadata for admin DTOs.
 * Use composition to avoid repeating audit fields across many admin DTOs.
 */
public record AuditableInfo(
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt
) {}


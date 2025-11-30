package com.makibeans.audit.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.time.Instant;

/**
 * Small value object to hold audit metadata for admin DTOs.
 * Use composition to avoid repeating audit fields across many admin DTOs.
 */

@JsonInclude(JsonInclude.Include.NON_NULL)
public record AuditableInfo(
        String createdBy,
        Instant createdAt,
        String updatedBy,
        Instant updatedAt,
        Boolean deleted,
        Instant deletedOn,
        String deletedBy,
        Instant deletedAt
) {}


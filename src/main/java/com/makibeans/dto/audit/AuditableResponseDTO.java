package com.makibeans.dto.audit;

import lombok.Data;
import java.time.Instant;

/**
 * Base DTO for audit fields.
 */
@Data
public abstract class AuditableResponseDTO {
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    private Boolean deleted;
    private Instant deletedAt;
    private String deletedBy;
}
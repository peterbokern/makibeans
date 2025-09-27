package com.makibeans.dto.base;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
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
}
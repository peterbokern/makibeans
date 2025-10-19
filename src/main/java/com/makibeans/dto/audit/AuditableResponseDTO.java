package com.makibeans.dto.audit;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonIgnoreType;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import java.time.Instant;

/**
 * Base DTO for audit fields.
 */
@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public abstract class AuditableResponseDTO {
    private String createdBy;
    private Instant createdAt;
    private String updatedBy;
    private Instant updatedAt;
    @JsonInclude(value= JsonInclude.Include.NON_DEFAULT) // Only include if not default (false for Boolean)
    private Boolean deleted;
    private Instant deletedAt;
    private String deletedBy;
}
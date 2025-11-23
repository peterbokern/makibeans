package com.makibeans.size.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.audit.dto.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Size responses.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "name", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class SizeResponseDTO extends AuditableResponseDTO {
    Long id;
    String name;
}

package com.makibeans.dto.attribute;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.dto.base.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Attribute Template response.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "attributeName", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class AttributeResponseDTO extends AuditableResponseDTO {
    private Long id;
    private String attributeName;
}


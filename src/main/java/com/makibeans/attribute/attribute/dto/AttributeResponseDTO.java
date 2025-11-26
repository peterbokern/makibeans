package com.makibeans.attribute.attribute.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.attribute.attribute.model.AttributeDataType;
import com.makibeans.audit.dto.AuditableResponseDTO;
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
@JsonPropertyOrder({"id", "name", "description", "createdBy", "createdAt", "updatedBy", "updatedAt", "isDeleted", "deletedAt", "deletedBy"})
public class AttributeResponseDTO extends AuditableResponseDTO {
    private Long id;
    private String name;
    private String description;
    private String dataType;
}


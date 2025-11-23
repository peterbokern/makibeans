package com.makibeans.dto.attributevalue;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.dto.audit.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for AttributeValue responses.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "attributeId", "attributeName", "value", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class AttributeValueResponseDTO extends AuditableResponseDTO {

    private Long id;
    private Long attributeId;
    private String attributeName;
    private String value;
}

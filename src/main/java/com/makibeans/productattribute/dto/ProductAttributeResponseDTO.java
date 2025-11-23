package com.makibeans.productattribute.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.attributevalue.dto.AttributeValueSimpleResponseDTO;
import com.makibeans.audit.dto.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.HashSet;
import java.util.Set;

/**
 * Data Transfer Object for ProductAttributeResponse.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "productId", "productName","attributeId", "attributeName", "values", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class ProductAttributeResponseDTO extends AuditableResponseDTO {

    private Long id;
    private Long productId;
    private String productName;
    private Long attributeId;
    private String attributeName;
    private Set<AttributeValueSimpleResponseDTO> values = new HashSet<>();
}

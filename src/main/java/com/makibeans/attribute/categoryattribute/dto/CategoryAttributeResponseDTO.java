package com.makibeans.attribute.categoryattribute.dto;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.audit.dto.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "categoryId", "categoryName", "attributeId", "attributeName","required", "createdBy", "createdAt", "updatedBy", "updatedAt", "deleted", "deletedAt", "deletedBy"})
public class CategoryAttributeResponseDTO extends AuditableResponseDTO {
    private Long id;
    private Long categoryId;
    private String categoryName;
    private Long attributeId;
    private String attributeName;
    private Boolean required;
}

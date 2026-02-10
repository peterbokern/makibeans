package com.makibeans.categoryattribute.dto;

import com.makibeans.audit.dto.AuditableInfo;

public record CategoryAttributeResponseDTO(
        Long categoryAttributeId,
        Long attributeId,
        String attributeName,
        Boolean required,
        Boolean filterable,
        Integer sortOrder
) {}
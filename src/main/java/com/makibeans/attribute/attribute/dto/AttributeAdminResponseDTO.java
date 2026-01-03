package com.makibeans.attribute.attribute.dto;

import com.makibeans.attribute.attributevalue.dto.AttributeValueAdminResponseDTO;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.audit.dto.AuditableInfo;

import java.util.List;

/**
 * Admin view for Attribute including audit fields.
 */
public record AttributeAdminResponseDTO(
        Long id,
        String name,
        String description,
        String dataType,
        String inputType,
        String slug,
        AuditableInfo audit
) {}

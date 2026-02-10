package com.makibeans.productattribute.dto;

import com.makibeans.productattributevalue.dto.ProductAttributeValueAdminResponseDTO;
import com.makibeans.audit.dto.AuditableInfo;

import java.util.List;

/**
 * Admin view for ProductAttribute including audit metadata.
 */
public record ProductAttributeAdminResponseDTO(
        Long id,
        Long categoryAttributeId,
        Long attributeId,
        String attributeName,
        String attributeSlug,
        String dataType,
        String inputType,
        List<ProductAttributeValueAdminResponseDTO> values,
        Boolean visible,
        AuditableInfo audit
) {}

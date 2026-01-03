package com.makibeans.attribute.productattribute.dto;

import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValueAdminResponseDTO;
import com.makibeans.audit.dto.AuditableInfo;

import java.util.List;

/**
 * Admin view for ProductAttribute including audit metadata.
 */
public record ProductAttributeAdminResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long categoryAttributeId,
        Long attributeId,
        String attributeName,
        List<ProductAttributeValueAdminResponseDTO> values,
        Boolean visible,
        AuditableInfo audit
) {}

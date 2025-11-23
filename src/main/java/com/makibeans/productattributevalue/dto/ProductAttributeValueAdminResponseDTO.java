package com.makibeans.productattributevalue.dto;

import com.makibeans.audit.dto.AuditableInfo;

/**
 * Admin view for ProductAttributeValue including audit info.
 */
public record ProductAttributeValueAdminResponseDTO(
        Long id,

        // Owner side (ProductAttribute)
        Long productAttributeId,

        // Product context
        Long productId,
        String productName,

        // Attribute (template) context
        Long attributeId,
        String attributeName,

        // Value side
        Long attributeValueId,
        String value,

        // Audit block (createdBy/At, updatedBy/At, etc.)
        AuditableInfo audit
) {}
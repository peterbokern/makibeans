package com.makibeans.dto.productattribute;

import com.makibeans.dto.audit.AuditableInfo;
import java.util.List;

/**
 * Admin view for ProductAttribute including audit metadata.
 */
public record ProductAttributeAdminResponseDTO(
        Long id,
        Long productId,
        Long attributeId,
        String attributeName,
        List<com.makibeans.dto.attributevalue.AttributeValuePublicResponseDTO> values,
        AuditableInfo audit
) {}

package com.makibeans.dto.productattribute;

import com.makibeans.dto.attributevalue.AttributeValueAdminResponseDTO;
import com.makibeans.dto.attributevalue.AttributeValueResponseDTO;
import com.makibeans.dto.audit.AuditableInfo;
import java.util.List;

/**
 * Admin view for ProductAttribute including audit metadata.
 */
public record ProductAttributeAdminResponseDTO(
        Long id,
        Long productId,
        String productName,
        Long attributeId,
        String attributeName,
        AuditableInfo audit
) {}

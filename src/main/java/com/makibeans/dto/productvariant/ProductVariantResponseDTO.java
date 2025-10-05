package com.makibeans.dto.productvariant;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.dto.audit.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for ProductVariant responses.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "sizeId", "sizeName", "sku", "priceInCents", "stock", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class ProductVariantResponseDTO extends AuditableResponseDTO {
    private Long id;
    private Long sizeId;
    private String sizeName;
    private String sku;
    private Long priceInCents;
    private Long stock;
}

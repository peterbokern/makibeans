package com.makibeans.dto.productattribute;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.makibeans.dto.attributevalue.AttributeValueSimpleResponseDTO;
import com.makibeans.dto.base.AuditableResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for ProductAttributeResponse.
 */

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonPropertyOrder({"id", "attributeId", "attributeName", "values", "createdBy", "createdAt", "updatedBy", "updatedAt"})
public class ProductAttributeResponseDTO extends AuditableResponseDTO {

    private Long id;
   /* private Long productId;
    private String productName;*/
    private Long attributeId;
    private String attributeName;
    private List<AttributeValueSimpleResponseDTO> values = new ArrayList<>();
}

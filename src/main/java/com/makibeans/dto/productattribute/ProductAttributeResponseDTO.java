package com.makibeans.dto.productattribute;

import com.makibeans.dto.attributevalue.AttributeValueSimpleResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * Data Transfer Object for ProductAttributeResponse.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponseDTO {

    private Long id;
   /* private Long productId;
    private String productName;*/
    private Long attributeTemplateId;
    private String attributeTemplateName;
    private List<AttributeValueSimpleResponseDTO> values = new ArrayList<>();
}

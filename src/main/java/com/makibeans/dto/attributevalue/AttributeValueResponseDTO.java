package com.makibeans.dto.attributevalue;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for AttributeValue responses.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValueResponseDTO {

    private Long id;
    private Long attributeTemplateId;
    private String attributeTemplateName;
    private String value;
}

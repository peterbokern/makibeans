package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductAttributeResponseDTO {

    private Long id;
    private Long attributeTemplateId;
    private String attributeTemplateName;
    private List<AttributeValueResponseDTO> attributeValues = new ArrayList<>();
}

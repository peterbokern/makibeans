package com.makibeans.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeValueResponseDTO {

    private Long id;
    private Long templateId;
    private String attributeName;
    private String value;
}

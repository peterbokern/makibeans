package com.makibeans.dto;

import lombok.Data;

@Data
public class AttributeValueResponseDTO {

    private Long id;
    private Long templateId;
    private String attributeName;
    private String value;
}

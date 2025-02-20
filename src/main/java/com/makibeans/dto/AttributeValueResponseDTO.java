package com.makibeans.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class AttributeValueResponseDTO {

    private Long id;
    private Long templateId;
    private String value;
}

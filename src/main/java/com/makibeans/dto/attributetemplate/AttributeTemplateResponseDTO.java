package com.makibeans.dto.attributetemplate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data Transfer Object for Attribute Template response.
 */

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AttributeTemplateResponseDTO {

    private Long id;
    private String name;
}


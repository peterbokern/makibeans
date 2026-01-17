package com.makibeans.category.filter.dto;


import com.makibeans.attribute.model.AttributeDataType;
import com.makibeans.attribute.model.AttributeInputType;

import java.util.List;

public record AttributeFilterDefinitionDTO(
        Long attributeId,
        String attributeName,
        String attributeSlug,
        Boolean required,
        Integer sortOrder,
        AttributeDataType dataType,
        AttributeInputType inputType,
        Boolean libraryBacked,
        List<AttributeValueFilterOptionDTO> values
) {}

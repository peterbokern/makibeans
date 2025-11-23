package com.makibeans.service.service;

import com.makibeans.dto.productattributevalue.ProductAttributeValueRequestDTO;
import com.makibeans.dto.productattributevalue.ProductAttributeValueResponseDTO;
import com.makibeans.model.ProductAttributeValue;
import jakarta.validation.Valid;

public interface ProductAttributeValueService extends CrudService<ProductAttributeValue, Long> {
    ProductAttributeValueResponseDTO getById(Long id);
    ProductAttributeValueResponseDTO create(@Valid ProductAttributeValueRequestDTO dto);

}

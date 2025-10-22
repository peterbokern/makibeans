package com.makibeans.service.service;

import com.makibeans.dto.productattributevalue.ProductAttributeValueRequestDTO;
import com.makibeans.dto.productattributevalue.ProductAttributeValueResponseDTO;
import com.makibeans.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeValueFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProductAttributeValueService extends CrudService<ProductAttributeValue, Long> {
    ProductAttributeValueResponseDTO getById(Long id);
    Page<ProductAttributeValueResponseDTO> search(SearchRequest<ProductAttributeValueFilter> request);
    ProductAttributeValueResponseDTO create(@Valid ProductAttributeValueRequestDTO dto);
    ProductAttributeValueResponseDTO update(Long id, @Valid ProductAttributeValueRequestDTO dto);
}

package com.makibeans.service.service;

import com.makibeans.dto.productattribute.ProductAttributeRequestDTO;
import com.makibeans.dto.productattribute.ProductAttributeResponseDTO;
import com.makibeans.model.ProductAttribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProductAttributeService extends CrudService<ProductAttribute, Long> {
    ProductAttribute getById(Long id);
    Page<ProductAttribute> search(SearchRequest<ProductAttributeFilter> request);
    ProductAttribute  create(@Valid ProductAttributeRequestDTO dto);
    //ProductAttributeResponseDTO update(Long id, @Valid ProductAttributeRequestDTO dto);
}

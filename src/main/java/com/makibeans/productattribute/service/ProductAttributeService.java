package com.makibeans.productattribute.service;

import com.makibeans.common.service.CrudService;
import com.makibeans.productattribute.dto.ProductAttributeRequestDTO;
import com.makibeans.productattribute.model.ProductAttribute;
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

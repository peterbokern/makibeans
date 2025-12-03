package com.makibeans.attribute.productattribute.service;

import com.makibeans.attribute.productattribute.dto.ProductAttributeRequestDTO;
import com.makibeans.attribute.productattribute.filter.ProductAttributeFilter;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.common.service.CrudService;
import com.makibeans.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProductAttributeService extends CrudService<ProductAttribute, Long> {
    ProductAttribute getById(Long id);
    Page<ProductAttribute> search(SearchRequest<ProductAttributeFilter> request);
    ProductAttribute  create(@Valid ProductAttributeRequestDTO dto);

    void disable(Long id);
    ProductAttribute enable(Long id);
    //ProductAttributeResponseDTO update(Long id, @Valid ProductAttributeRequestDTO dto);
}

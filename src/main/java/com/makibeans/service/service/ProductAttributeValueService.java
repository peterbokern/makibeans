package com.makibeans.service.service;

import com.makibeans.dto.productattributevalue.ProductAttributeValueRequestDTO;
import com.makibeans.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeValueFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProductAttributeValueService extends CrudService<ProductAttributeValue, Long> {

    ProductAttributeValue getById(Long id);

    ProductAttributeValue create(@Valid ProductAttributeValueRequestDTO dto);

    Page<ProductAttributeValue> search(SearchRequest<ProductAttributeValueFilter> request);
}

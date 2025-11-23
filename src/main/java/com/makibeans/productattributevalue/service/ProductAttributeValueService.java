package com.makibeans.productattributevalue.service;

import com.makibeans.common.service.CrudService;
import com.makibeans.productattributevalue.dto.ProductAttributeValueRequestDTO;
import com.makibeans.productattributevalue.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductAttributeValueFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProductAttributeValueService extends CrudService<ProductAttributeValue, Long> {

    ProductAttributeValue getById(Long id);

    ProductAttributeValue create(@Valid ProductAttributeValueRequestDTO dto);

    Page<ProductAttributeValue> search(SearchRequest<ProductAttributeValueFilter> request);
}

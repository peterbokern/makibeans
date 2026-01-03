package com.makibeans.attribute.productattributevalue.service;

import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValueRequestDTO;
import com.makibeans.attribute.productattributevalue.filter.ProductAttributeValueFilter;
import com.makibeans.attribute.productattributevalue.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ProductAttributeValueService  {

    ProductAttributeValue getById(Long id);

    ProductAttributeValue create(@Valid ProductAttributeValueRequestDTO dto);

    Page<ProductAttributeValue> search(SearchRequest<ProductAttributeValueFilter> request);

    @Transactional
    void delete(Long id);
}

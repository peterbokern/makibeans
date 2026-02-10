package com.makibeans.productattributevalue.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.productattributevalue.dto.ProductAttributeValueRequestDTO;
import com.makibeans.productattributevalue.filter.ProductAttributeValueFilter;
import com.makibeans.productattributevalue.model.ProductAttributeValue;
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

    @Transactional
    void deleteByProductId(Long productId, DeleteReason reason);
}

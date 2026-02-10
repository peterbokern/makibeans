package com.makibeans.productattribute.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.productattribute.dto.ProductAttributeRequestDTO;
import com.makibeans.productattribute.dto.ProductAttributeUpdateDTO;
import com.makibeans.productattribute.filter.ProductAttributeAdminFilter;
import com.makibeans.productattribute.filter.ProductAttributePublicFilter;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ProductAttributeService {
    ProductAttribute getById(Long id);

    @Transactional(readOnly = true)
    Page<ProductAttribute> searchPublic(SearchRequest<ProductAttributePublicFilter> req);

    @Transactional(readOnly = true)
    Page<ProductAttribute> searchAdmin(SearchRequest<ProductAttributeAdminFilter> req);

    @Transactional(readOnly = true)
    <F> Page<ProductAttribute> search(SearchRequest<F> req, Class<F> filterClass);

    ProductAttribute  create(@Valid ProductAttributeRequestDTO dto);
    ProductAttribute update(Long id, @Valid ProductAttributeUpdateDTO dto);
    void delete(Long id);

    @Transactional
    void deleteByProductId(Long productId, DeleteReason reason);
}

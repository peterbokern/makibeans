package com.makibeans.productvariant.service;

import com.makibeans.common.service.CrudService;
import com.makibeans.productvariant.dto.ProductVariantRequestDTO;
import com.makibeans.productvariant.dto.ProductVariantResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantUpdateDTO;
import com.makibeans.productvariant.filter.ProductVariantAdminFilter;
import com.makibeans.productvariant.filter.ProductVariantPublicFilter;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.search.SearchRequest;
import com.makibeans.productvariant.filter.ProductVariantFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ProductVariantService extends CrudService<ProductVariant, Long> {

    ProductVariant getById(Long id);

    @Transactional(readOnly = true)
    Page<ProductVariant> searchPublic(SearchRequest<ProductVariantPublicFilter> req);

    @Transactional(readOnly = true)
    Page<ProductVariant> searchAdmin(SearchRequest<ProductVariantAdminFilter> req);

    @Transactional(readOnly = true)
    <F> Page<ProductVariant> search(SearchRequest<F> req, Class<F> filterClass);

    ProductVariant create(@Valid ProductVariantRequestDTO dto);

    ProductVariant update(Long id, @Valid ProductVariantUpdateDTO dto);

    @Transactional
    ProductVariantResponseDTO setStock(Long variantId, Long stock);

    @Transactional
    ProductVariantResponseDTO incrementStock(Long variantId, Long by);

    @Transactional
    ProductVariantResponseDTO decrementStock(Long variantId, Long by);
}

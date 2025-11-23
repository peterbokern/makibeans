package com.makibeans.service.service;

import com.makibeans.dto.productvariant.ProductVariantRequestDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.model.ProductVariant;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductVariantFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

public interface ProductVariantService extends CrudService<ProductVariant, Long> {

    ProductVariant getById(Long id);

    Page<ProductVariant> search(SearchRequest<ProductVariantFilter> request);

    ProductVariant create(@Valid ProductVariantRequestDTO dto);

    ProductVariant update(Long id, @Valid ProductVariantUpdateDTO dto);

    @Transactional
    ProductVariantResponseDTO setStock(Long variantId, Long stock);

    @Transactional
    ProductVariantResponseDTO incrementStock(Long variantId, Long by);

    @Transactional
    ProductVariantResponseDTO decrementStock(Long variantId, Long by);
}

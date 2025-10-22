package com.makibeans.service.service;

import com.makibeans.dto.productvariant.ProductVariantRequestDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductVariantFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

public interface ProductVariantService extends CrudService<ProductVariant, Long> {
    ProductVariantResponseDTO getById(Long id);
    Page<ProductVariantResponseDTO> search(SearchRequest<ProductVariantFilter> request);
    ProductVariantResponseDTO create(@Valid ProductVariantRequestDTO dto);
    ProductVariantResponseDTO update(Long id, @Valid ProductVariantUpdateDTO dto);

    // Relevant convenience methods to keep
    ProductVariantResponseDTO setStock(Long variantId, Long stock);
    ProductVariantResponseDTO incrementStock(Long variantId, Long by);
    ProductVariantResponseDTO decrementStock(Long variantId, Long by);
}

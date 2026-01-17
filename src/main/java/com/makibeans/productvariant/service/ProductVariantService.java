package com.makibeans.productvariant.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.productvariant.dto.ProductVariantAdminResponseDTO;
import com.makibeans.productvariant.dto.ProductVariantRequestDTO;
import com.makibeans.productvariant.dto.ProductVariantUpdateDTO;
import com.makibeans.productvariant.filter.ProductVariantAdminFilter;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.search.SearchRequest;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ProductVariantService  {

    ProductVariant getById(Long id);

    Page<ProductVariant> searchAdmin(SearchRequest<ProductVariantAdminFilter> req);

    <F> Page<ProductVariant> search(SearchRequest<F> req, Class<F> filterClass);

    List<ProductVariant> getPublicVariantsByProductId(Long productId);

    ProductVariant create(@Valid ProductVariantRequestDTO dto);

    ProductVariant update(Long id, @Valid ProductVariantUpdateDTO dto);

    void delete(Long id);

    void deleteByProductId(Long productId, DeleteReason reason);

    void setDefault(Long productId, Long productVariantId);

    ProductVariantAdminResponseDTO setStock(Long variantId, Long stock);

    ProductVariantAdminResponseDTO incrementStock(Long variantId, Long by);

    ProductVariantAdminResponseDTO decrementStock(Long variantId, Long by);
}

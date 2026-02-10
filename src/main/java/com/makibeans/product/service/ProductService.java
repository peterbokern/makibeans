package com.makibeans.product.service;

import com.makibeans.product.dto.ProductAdminResponseDTO;
import com.makibeans.product.dto.ProductPublicResponseDTO;
import com.makibeans.product.dto.ProductRequestDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.filter.ProductAdminFilter;
import com.makibeans.product.model.Product;
import com.makibeans.product.repository.PriceRange;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.search.SearchRequest;
import com.makibeans.product.filter.ProductPublicFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface ProductService  {

    @Transactional(readOnly = true)
    Product findById(Long id);

    @Transactional(readOnly = true)
    Product findByIdIncludingDeleted(Long id);

    ProductPublicResponseDTO getById(Long id);

    ProductAdminResponseDTO  getByIdIncludingDeleted(Long id);

    Page<ProductPublicResponseDTO> searchPublic(SearchRequest<ProductPublicFilter> req);

    Page<ProductAdminResponseDTO> searchAdmin(SearchRequest<ProductAdminFilter> req);

    ProductAdminResponseDTO create(@Valid ProductRequestDTO dto);

    ProductAdminResponseDTO update(Long id, @Valid ProductUpdateDTO dto);

    void delete(Long id);

    byte[] getProductImage(Long productId);

    void deleteProductImage(Long productId);

    void restore(Long productId);

    ProductAdminResponseDTO uploadProductImage(Long productId, MultipartFile image);

    Map<Long, PriceRange> getPriceRangesForProducts(List<Long> productIds);

}

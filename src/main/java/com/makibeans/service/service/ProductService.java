package com.makibeans.service.service;

import com.makibeans.dto.product.ProductRequestDTO;
import com.makibeans.dto.product.ProductResponseDTO;
import com.makibeans.dto.product.ProductUpdateDTO;
import com.makibeans.model.Product;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductFilter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService extends CrudService<Product, Long> {
    ProductResponseDTO getById(Long id);
    Page<ProductResponseDTO> search(SearchRequest<ProductFilter> request);
    ProductResponseDTO create(@Valid ProductRequestDTO dto);
    ProductResponseDTO update(Long id, @Valid ProductUpdateDTO dto);
    Boolean existByCategoryId(Long categoryId);
    byte[] getProductImage(Long productId);
    void deleteProductImage(Long productId);
    ProductResponseDTO uploadProductImage(Long productId, MultipartFile image);
}

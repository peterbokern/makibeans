package com.makibeans.product.service;

import com.makibeans.product.dto.ProductRequestDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.model.Product;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.filters.ProductFilter;
import com.makibeans.common.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService extends CrudService<Product, Long> {

    Product getById(Long id);

    Page<Product> search(SearchRequest<ProductFilter> request);

    Product create(@Valid ProductRequestDTO dto);

    Product update(Long id, @Valid ProductUpdateDTO dto);

    Boolean existByCategoryId(Long categoryId);

    byte[] getProductImage(Long productId);

    void deleteProductImage(Long productId);

    Product uploadProductImage(Long productId, MultipartFile image);

    Boolean existsByCategoryId(Long categoryId);
}

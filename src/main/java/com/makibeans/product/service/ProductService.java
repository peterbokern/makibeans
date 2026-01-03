package com.makibeans.product.service;

import com.makibeans.product.dto.ProductRequestDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.filter.ProductAdminFilter;
import com.makibeans.product.model.Product;
import com.makibeans.search.SearchRequest;
import com.makibeans.product.filter.ProductFilter;
import com.makibeans.product.filter.ProductPublicFilter;
import com.makibeans.common.service.CrudService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

public interface ProductService  {

    Product getById(Long id);

    @Transactional
    Product getByIdIncludingDeleted(Long id);

    @Transactional(readOnly = true)
    Page<Product> searchPublic(SearchRequest<ProductPublicFilter> req);

    @Transactional(readOnly = true)
    Page<Product> searchAdmin(SearchRequest<ProductAdminFilter> req);

    @Transactional(readOnly = true)
    <F> Page<Product> search(SearchRequest<F> req, Class<F> filterClass);

    Product create(@Valid ProductRequestDTO dto);

    Product update(Long id, @Valid ProductUpdateDTO dto);

    void delete(Long id);

    byte[] getProductImage(Long productId);

    void deleteProductImage(Long productId);

    @Transactional
    void restore(Long productId);

    Product uploadProductImage(Long productId, MultipartFile image);
}

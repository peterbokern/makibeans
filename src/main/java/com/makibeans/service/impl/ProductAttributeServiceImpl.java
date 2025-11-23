package com.makibeans.service.impl;

import com.makibeans.dto.productattribute.ProductAttributeRequestDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.ProductAttributeMapper;
import com.makibeans.model.*;
import com.makibeans.product.model.Product;
import com.makibeans.product.service.ProductService;
import com.makibeans.repository.ProductAttributeRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.search.filters.ProductAttributeFilter;
import com.makibeans.service.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductAttributeServiceImpl implements ProductAttributeService, CrudService<ProductAttribute, Long> {

    private final ProductAttributeRepository repo;
    private final ProductService productService;
    private final AttributeService attributeService;

    @Autowired
    public ProductAttributeServiceImpl(ProductAttributeRepository repo,
                                       ProductService productService,
                                       AttributeService attributeService,
                                       ProductAttributeMapper mapper) {
        this.repo = repo;
        this.productService = productService;
        this.attributeService = attributeService;
    }

    @Transactional(readOnly = true)
    public ProductAttribute getById(Long id) {
        return getOrThrow(id);
    }

    @Transactional
    public Page<ProductAttribute> search(SearchRequest<ProductAttributeFilter> req) {
        Specification<ProductAttribute> spec =
                SpecificationFactory.fromRequest(req, ProductAttributeFilter.class);

        Sort sort = new SortResolver(ProductAttributeFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    @Transactional
    public ProductAttribute create(ProductAttributeRequestDTO requestDTO) {

        Long productId = requestDTO.getProductId();
        Long attributeId = requestDTO.getAttributeId();

        Product product = productService.getOrThrow(productId);
        Attribute attribute = attributeService.getOrThrow(attributeId);

        if (repo.existsByProductIdAndAttributeId(productId, attributeId)) {
            throw new DuplicateResourceException("ProductAttribute with Product ID " + productId +
                    " and Attribute ID " + attributeId + " already exists.");
        }

        ProductAttribute productAttribute = new ProductAttribute(attribute, product);

        return repo.save(productAttribute);
    }

    @Transactional
    public void delete(Long productAttributeId) {
        getOrThrow(productAttributeId);
        softDelete(productAttributeId);
    }

    /**
     * Implementors must return their repository.
     */
    @Override
    public JpaRepository<ProductAttribute, Long> repo() {
        return repo;
    }
}

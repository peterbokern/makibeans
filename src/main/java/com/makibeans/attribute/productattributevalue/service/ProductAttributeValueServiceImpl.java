package com.makibeans.attribute.productattributevalue.service;

import com.makibeans.attribute.productattributevalue.repository.ProductAttributeValueRepository;
import com.makibeans.attribute.productattributevalue.dto.ProductAttributeValueRequestDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.attribute.productattributevalue.mapper.ProductAttributeValueMapper;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.attribute.productattributevalue.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.attribute.productattributevalue.filter.ProductAttributeValueFilter;
import com.makibeans.attribute.attributevalue.service.AttributeValueService;
import com.makibeans.common.service.CrudService;
import com.makibeans.attribute.productattribute.service.ProductAttributeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductAttributeValueServiceImpl implements CrudService<ProductAttributeValue, Long>, ProductAttributeValueService {

    private final ProductAttributeValueRepository repo;
    private final ProductAttributeService productAttributeService;
    private final AttributeValueService attributeValueService;

    public ProductAttributeValueServiceImpl(ProductAttributeValueRepository repo, ProductAttributeService productAttributeService, AttributeValueService attributeValueService, ProductAttributeValueMapper mapper) {
        this.repo = repo;
        this.productAttributeService = productAttributeService;
        this.attributeValueService = attributeValueService;
    }

    /**
     * Implementors must return their repository.
     */
    @Override
    public JpaRepository<ProductAttributeValue, Long> repo() {
        return this.repo;
    }

    @Override
    @Transactional(readOnly = true)
    public ProductAttributeValue getById(Long id) {
        return getOrThrow(id);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAttributeValue> search(SearchRequest<ProductAttributeValueFilter> req) {

        Specification<ProductAttributeValue> spec =
                SpecificationFactory.fromRequest(req, ProductAttributeValueFilter.class);

        Sort sort = new SortResolver(ProductAttributeValueFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    @Override
    @Transactional
    public ProductAttributeValue create(ProductAttributeValueRequestDTO dto) {
        ProductAttributeValue entity = new ProductAttributeValue();

        ProductAttribute productAttribute = productAttributeService.getOrThrow(dto.getProductAttributeId());
        AttributeValue attributeValue = attributeValueService.getOrThrow(dto.getAttributeValueId());

        if (repo.existsByProductAttributeIdAndAttributeValueId(dto.getProductAttributeId(), dto.getAttributeValueId())) {
            throw new DuplicateResourceException("Duplicate ProductAttributeValue link");
        }

        entity.setProductAttribute(productAttribute);
        entity.setAttributeValue(attributeValue);

        return repo.save(entity);
    }

    @Override
    public void delete(Long id) {
        hardDelete(id);
    }
}

package com.makibeans.productattributevalue.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.productattributevalue.repository.ProductAttributeValueRepository;
import com.makibeans.productattributevalue.dto.ProductAttributeValueRequestDTO;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.productattributevalue.mapper.ProductAttributeValueMapper;
import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.productattributevalue.model.ProductAttributeValue;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.productattributevalue.filter.ProductAttributeValueFilter;
import com.makibeans.attributevalue.service.AttributeValueService;
import com.makibeans.productattribute.service.ProductAttributeService;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ProductAttributeValueServiceImpl implements ProductAttributeValueService {

    private final ProductAttributeValueRepository repo;
    private final ProductAttributeService productAttributeService;
    private final AttributeValueService attributeValueService;

    public ProductAttributeValueServiceImpl(ProductAttributeValueRepository repo, ProductAttributeService productAttributeService, AttributeValueService attributeValueService, ProductAttributeValueMapper mapper) {
        this.repo = repo;
        this.productAttributeService = productAttributeService;
        this.attributeValueService = attributeValueService;
    }

    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    /**
     * Retrieves a ProductAttributeValue by its ID.
     *
     * @param id the ID of the ProductAttributeValue
     * @return the ProductAttributeValue entity
     * @throws ResourceNotFoundException if the ProductAttributeValue is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductAttributeValue getById(Long id) {

        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAttributeValue with ID " + id + " not found."));
    }

    // -------------------------------------------------------------------------
    // SEARCH
    // -------------------------------------------------------------------------

    /**
     * Searches for ProductAttributeValues based on the provided search request.
     *
     * @param req the search request containing filters, pagination, and sorting information
     * @return a paginated list of ProductAttributeValues matching the search criteria
     */
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

    // -------------------------------------------------------------------------
    // WRITES
    // -------------------------------------------------------------------------

    /**
     * Creates a new ProductAttributeValue based on the provided DTO.
     *
     * @param dto the DTO containing data for the new ProductAttributeValue
     * @return the created ProductAttributeValue entity
     * @throws DuplicateResourceException if a duplicate ProductAttributeValue link exists
     */
    @Override
    @Transactional
    public ProductAttributeValue create(ProductAttributeValueRequestDTO dto) {

        ProductAttribute productAttribute = productAttributeService.getById(dto.productAttributeId());
        AttributeValue attributeValue = attributeValueService.getById(dto.attributeValueId());

        asserUniqueLink(productAttribute.getId(), attributeValue.getId());

        ProductAttributeValue pav = new ProductAttributeValue();
        pav.setProductAttribute(productAttribute);
        pav.setAttributeValue(attributeValue);

        return repo.save(pav);
    }

    /**
     * Deletes a ProductAttributeValue by its ID.
     *
     * @param id the ID of the ProductAttributeValue to delete
     */
    @Override
    @Transactional
    public void delete(Long id) {
        ProductAttributeValue pav = getById(id);
        if (!pav.isDeleted()) pav.setDeleted(true);
    }

    @Override
    @Transactional
    public void deleteByProductId(Long productId, DeleteReason reason) {
        List<ProductAttributeValue> values = repo.findByProductAttributeProductIdAndDeletedFalse(productId);
        values.forEach(pav -> {
            if (!pav.isDeleted()) {
                pav.setDeleted(true);
                pav.setDeletedReason(reason);
            }
        });
    }

    // -------------------------------------------------------------------------
    // VALIDATIONS
    // -------------------------------------------------------------------------

    /**
     * Asserts that the link between ProductAttribute and AttributeValue is unique.
     *
     * @param productAttributeId the ID of the ProductAttribute
     * @param attributeValueId   the ID of the AttributeValue
     * @throws DuplicateResourceException if a duplicate link exists
     */
    private void asserUniqueLink(Long productAttributeId, Long attributeValueId) {
        if (repo.existsByProductAttributeIdAndAttributeValueId(productAttributeId, attributeValueId)) {
            throw new DuplicateResourceException("Duplicate ProductAttributeValue link");
        }
    }
}

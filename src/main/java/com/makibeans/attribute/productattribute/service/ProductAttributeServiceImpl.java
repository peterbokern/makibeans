package com.makibeans.attribute.productattribute.service;

import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.attribute.categoryattribute.service.CategoryAttributeService;
import com.makibeans.attribute.productattribute.dto.ProductAttributeUpdateDTO;
import com.makibeans.attribute.productattribute.filter.ProductAttributeAdminFilter;
import com.makibeans.attribute.productattribute.filter.ProductAttributePublicFilter;
import com.makibeans.attribute.productattributevalue.repository.ProductAttributeValueRepository;
import com.makibeans.attribute.productattribute.dto.ProductAttributeRequestDTO;
import com.makibeans.web.exceptions.BadRequestException;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.product.model.Product;
import com.makibeans.product.service.ProductService;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import com.makibeans.attribute.productattribute.repository.ProductAttributeRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class ProductAttributeServiceImpl implements ProductAttributeService {

    private final ProductAttributeRepository repo;
    private final ProductService productService;
    private final CategoryAttributeService categoryAttributeService;

    @Autowired
    public ProductAttributeServiceImpl(ProductAttributeRepository repo,
                                       ProductService productService,
                                       CategoryAttributeService categoryAttributeService, ProductAttributeValueRepository productAttributeValueRepository) {
        this.repo = repo;
        this.productService = productService;
        this.categoryAttributeService = categoryAttributeService;
    }

    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    /**
     * Retrieves a ProductAttribute by its ID.
     *
     * @param id the ID of the ProductAttribute
     * @return the ProductAttribute entity
     * @throws ResourceNotFoundException if the ProductAttribute is not found
     */
    @Override
    @Transactional(readOnly = true)
    public ProductAttribute getById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("ProductAttribute with ID " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAttribute> searchPublic(SearchRequest<ProductAttributePublicFilter> req) {
        return search(req, ProductAttributePublicFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAttribute> searchAdmin(SearchRequest<ProductAttributeAdminFilter> req) {
        return search(req, ProductAttributeAdminFilter.class);
    }


    @Override
    @Transactional(readOnly = true)
    public <F> Page<ProductAttribute> search(SearchRequest<F> req, Class<F> filterClass) {
        Specification<ProductAttribute> spec =
                SpecificationFactory.fromRequest(req, filterClass);

        Sort sort = new SortResolver(filterClass)
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
     * Creates a new ProductAttribute based on the provided request DTO.
     *
     * @param requestDTO the DTO containing the details for the new ProductAttribute
     * @return the created ProductAttribute entity
     * @throws IllegalStateException if the CategoryAttribute does not belong to the Product's category
     * @throws DuplicateResourceException if a ProductAttribute with the same Product and CategoryAttribute already exists
     */

    @Override
    @Transactional
    public ProductAttribute create(ProductAttributeRequestDTO requestDTO) {

        Long productId = requestDTO.getProductId();
        Long categoryAttributeId = requestDTO.getCategoryAttributeId();

        Product product = productService.getById(productId);
        CategoryAttribute categoryAttribute = categoryAttributeService.getById(categoryAttributeId);

        assertValidCategory(product, categoryAttribute);
        assertUniqueProductAttribute(productId, categoryAttributeId);

        ProductAttribute productAttribute = new ProductAttribute();
        productAttribute.setProduct(product);
        productAttribute.setCategoryAttribute(categoryAttribute);
        productAttribute.setVisible(true);

        return repo.save(productAttribute);
    }

    /**
     * Updates an existing ProductAttribute with the provided update DTO.
     *
     * @param id the ID of the ProductAttribute to update
     * @param dto the DTO containing the updated details
     * @return the updated ProductAttribute entity
     * @throws ResourceNotFoundException if the ProductAttribute is not found
     */
    @Override
    @Transactional
    public ProductAttribute update(Long id, ProductAttributeUpdateDTO dto) {
        ProductAttribute productAttribute = getById(id);

        if (dto.visible() != null) {
            productAttribute.setVisible(dto.visible());
        }

        return repo.save(productAttribute);
    }


    /**
     * Deletes a ProductAttribute by marking it and its values as deleted.
     *
     * @param productAttributeId the ID of the ProductAttribute to delete
     */
    @Override
    @Transactional
    public void delete(Long productAttributeId) {
        ProductAttribute productAttribute = getById(productAttributeId);
        if (productAttribute.getCategoryAttribute().isRequired()) {
            throw new BadRequestException("Cannot delete required ProductAttribute with ID " + productAttributeId);
        }
        repo.delete(productAttribute); //cascade on ProductAttributeValue will handle deletion of values
    }

    // -------------------------------------------------------------------------
    // VALIDATIONS
    // -------------------------------------------------------------------------

    /**
     * Validates that the CategoryAttribute belongs to the same category as the Product.
     *
     * @param product the Product entity
     * @param categoryAttribute the CategoryAttribute entity
     * @throws IllegalStateException if the CategoryAttribute does not belong to the Product's category
     */
    private void assertValidCategory(Product product, CategoryAttribute categoryAttribute) {
        Long categoryId = product.getCategory().getId();
        Long attributeCategoryId = categoryAttribute.getCategory().getId();

        if (!categoryId.equals(attributeCategoryId)) {
            throw new BadRequestException("Cannot assign CategoryAttribute ID " + categoryAttribute.getId() +
                    " to Product ID " + product.getId() + " as it belongs to a different category.");
        }
    }

    /**
     * Validates that a ProductAttribute with the same Product and CategoryAttribute does not already exist.
     *
     * @param productId the ID of the Product
     * @param categoryAttributeId the ID of the CategoryAttribute
     * @throws DuplicateResourceException if a ProductAttribute with the same Product and CategoryAttribute already exists
     */
    private void assertUniqueProductAttribute(Long productId, Long categoryAttributeId) {
        boolean exists = repo.existsByProductIdAndCategoryAttributeId(productId, categoryAttributeId);
        if (exists) {
            throw new DuplicateResourceException("ProductAttribute with Product ID " + productId +
                    " and CategoryAttribute ID " + categoryAttributeId + " already exists.");
        }
    }
}

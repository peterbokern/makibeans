// language: java
package com.makibeans.attribute.categoryattribute.service;

import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.attribute.categoryattribute.dto.CategoryAttributeUsageDTO;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributeAdminFilter;
import com.makibeans.attribute.categoryattribute.filter.CategoryAttributePublicFilter;
import com.makibeans.attribute.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.attribute.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.attribute.productattribute.repository.ProductAttributeRepository;
import com.makibeans.search.*;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.category.model.Category;
import com.makibeans.attribute.attribute.service.AttributeService;
import com.makibeans.category.service.CategoryService;
import com.makibeans.web.exceptions.ResourceInUseException;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;

@Service
@Transactional
public class CategoryAttributeServiceImpl implements CategoryAttributeService {

    private final CategoryAttributeRepository repo;
    private final CategoryService categoryService;
    private final AttributeService attributeService;
    private final CategoryAttributeMapper mapper;
    private final ProductAttributeRepository productAttributeRepository;

    @Autowired
    public CategoryAttributeServiceImpl(CategoryAttributeRepository repo,
                                        @Lazy CategoryService categoryService,
                                        AttributeService attributeService,
                                        CategoryAttributeMapper mapper, ProductAttributeRepository productAttributeRepository) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.attributeService = attributeService;
        this.mapper = mapper;
        this.productAttributeRepository = productAttributeRepository;
    }

    // -------------------------------------------------------------------------
    // READS
    // -------------------------------------------------------------------------

    /**
     * Retrieves a CategoryAttribute by its ID.
     * Throws ResourceNotFoundException if not found.
     */
    @Override
    @Transactional(readOnly = true)
    public CategoryAttribute getById(Long id) {
        return repo.findByIdAndDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("CategoryAttribute with id " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryAttribute getByIdIncludingDeleted(Long id) {
        return repo.findById(id).orElseThrow(() -> new ResourceNotFoundException("CategoryAttribute with id " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryAttribute> searchPublic(SearchRequest<CategoryAttributePublicFilter> req) {
        return search(req, CategoryAttributePublicFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<CategoryAttribute> searchAdmin(SearchRequest<CategoryAttributeAdminFilter> req) {
        return search(req, CategoryAttributeAdminFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <F> Page<CategoryAttribute> search(SearchRequest<F> req, Class<F> filterClass) {
        Specification<CategoryAttribute> spec =
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

    @Override
    @Transactional(readOnly = true)
    public CategoryAttributeUsageDTO summarizeCategoryAttributeUsage(Long id) {
        CategoryAttribute ca = getById(id);
        boolean used = productAttributeRepository.existsByCategoryAttributeAndDeletedFalse(ca);
        return new CategoryAttributeUsageDTO(used);
    }

    // -------------------------------------------------------------------------
    // WRITES
    // -------------------------------------------------------------------------

    /**
     * Creates a new CategoryAttribute association between a category and an attribute.
     */
    @Override
    @Transactional
    public CategoryAttribute create(@Valid CategoryAttributeRequestDTO requestDTO) {
        Category category = categoryService.getById(requestDTO.getCategoryId());
        Attribute attribute = attributeService.getById(requestDTO.getAttributeId());

        assertUniqueAssociation(category, attribute);

        int sortOrder = resolveSortOrder(category, requestDTO.getSortOrder());

        CategoryAttribute categoryAttribute = CategoryAttribute.builder()
                .category(category)
                .attribute(attribute)
                .sortOrder(sortOrder)
                .required(requestDTO.getRequired())
                .build();

        return repo.save(categoryAttribute);
    }

    /**
     * Updates an existing CategoryAttribute.
     */
    @Override
    @Transactional
    public CategoryAttribute update(Long id, @Valid CategoryAttributeUpdateDTO updateDTO) {
        CategoryAttribute categoryAttribute = getById(id);

        updateSortOrderIfChanged(categoryAttribute, categoryAttribute.getCategory(), updateDTO);

        mapper.updateEntityFromDTO(updateDTO, categoryAttribute);

        return categoryAttribute;
    }

    /**
     * Deletes a CategoryAttribute association.
     * Throws ResourceInUseException if the association is still in use.
     */
    @Override
    @Transactional
    public void delete(Long id) {
        CategoryAttribute categoryAttribute = getById(id);
        if (Boolean.TRUE.equals(categoryAttribute.isDeleted())) return;
        assertNotInUse(categoryAttribute);
        categoryAttribute.setDeleted(true);
    }

    @Override
    @Transactional
    public void deleteByCategory(Category category) {
        if (productAttributeRepository.existsByCategoryAttributeCategoryAndDeletedFalse(category)) {
            throw new ResourceInUseException(
                    "Cannot delete category attributes for category '" + category.getName() +
                            "' because they are still used by one or more products.");
        }
        Set<CategoryAttribute> categoryAttributes = repo.findByCategoryIdAndDeletedFalse(category.getId());
        categoryAttributes.forEach(ca -> ca.setDeleted(true));
    }

    /**
     * Restores a soft-deleted CategoryAttribute association.
     */
    @Override
    @Transactional
    public CategoryAttribute restore(Long id) throws BadRequestException {
        CategoryAttribute categoryAttribute = getByIdIncludingDeleted(id);
        assertDeleted(categoryAttribute);
        assertUniqueAssociationForRestore(categoryAttribute.getCategory(), categoryAttribute.getAttribute());
        assertCategoryNotDeleted(categoryAttribute.getCategory());
        categoryAttribute.setDeleted(false);
        return categoryAttribute;
    }

    @Override
    @Transactional
    public void restoreByCategory(Category category) {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        repo.restoreByCategoryId(category.getId());
    }


    // -------------------------------------------------------------------------
    // OTHER
    // -------------------------------------------------------------------------

    /**
     * Retrieves a CategoryAttribute by its ID or throws ResourceNotFoundException.
     */
    private void assertUniqueAssociation(Category category, Attribute attribute) {
        boolean exists = repo.existsByCategoryAndAttributeAndDeletedFalse(category, attribute);
        if (exists) {
            throw new DuplicateResourceException("Association between category " + category.getName() + " and attribute " + attribute.getName() + " already exists.");
        }
    }

    /**
     * Asserts that no active association exists for the given category and attribute when restoring.
     * Throws DuplicateResourceException if an active association exists.
     */
    private void assertUniqueAssociationForRestore(Category category, Attribute attribute) {
        boolean exists = repo.existsByCategoryAndAttributeAndDeletedFalse(category, attribute);
        if (exists) {
            throw new DuplicateResourceException("Cannot restore association between category " + category.getName() + " and attribute " + attribute.getName() + " because an active association already exists.");
        }
    }

    /**
     * Asserts that the CategoryAttribute is not in use by any products.
     * Throws ResourceInUseException if it is in use.
     */
    private void assertNotInUse(CategoryAttribute ca) {
        boolean jnUse = productAttributeRepository.existsByCategoryAttributeAndDeletedFalse(ca);
        if (jnUse) {
            throw new ResourceInUseException(
                    "Cannot remove attribute '" + ca.getAttribute().getName() +
                            "' from category '" + ca.getCategory().getName() +
                            "' because it is still used by one or more products.");
        }
    }

    /**
     * Asserts that the CategoryAttribute is marked as deleted.
     * Throws IllegalStateException if it is not deleted.
     */
    private void assertDeleted(CategoryAttribute ca) throws BadRequestException {
        if (!ca.isDeleted()) {
            throw new BadRequestException(String.format("Unable to restore CategoryAttribute with id %d because it is not deleted.", ca.getId()));
        }
    }

    private void assertCategoryNotDeleted(Category category) throws BadRequestException {
        if (category.isDeleted()) {
            throw new BadRequestException("Cannot restore CategoryAttribute because the associated category '" + category.getName() + "' is deleted.");
        }
    }

    // -------------------------------------------------------------------------
    // SORT ORDER HANDLING
    // -------------------------------------------------------------------------

    /**
     * Updates the sort order of a CategoryAttribute if it has changed.
     */
    private void updateSortOrderIfChanged(
            CategoryAttribute ca,
            Category category,
            CategoryAttributeUpdateDTO dto) {

        Integer currentSortOrder = ca.getSortOrder();
        Integer requestedSortOrder = dto.getSortOrder();

        // No change requested
        if (requestedSortOrder == null || requestedSortOrder.equals(currentSortOrder)) {
            return;
        }

        int newSortOrder = resolveSortOrder(category, requestedSortOrder);
        ca.setSortOrder(newSortOrder);
    }

    /**
     * Resolves the appropriate sort order for a CategoryAttribute.
     * Adjusts existing sort orders if necessary.
     */
    private int resolveSortOrder(Category category, Integer requestedSortOrder) {

        int maxSortOrder = repo.findMaxSortOrderByCategory(category).orElse(-1);
        if (isValidSortOrderRequest(requestedSortOrder, maxSortOrder)) {
            adjustSortOrdersForInsert(category, requestedSortOrder);
            return requestedSortOrder;
        } else {
            return maxSortOrder + 1;
        }
    }

    /**
     * Validates if the requested sort order is within acceptable bounds.
     */
    private boolean isValidSortOrderRequest(Integer requestedSortOrder, int maxSortOrder) {
        return requestedSortOrder != null &&
                requestedSortOrder >= 0 &&
                requestedSortOrder <= maxSortOrder;
    }

    /**
     * Adjusts existing sort orders to accommodate a new insertion.
     */
    private void adjustSortOrdersForInsert(Category category, int fromSortOrder) {
        List<CategoryAttribute> valuesToAdjust =
                repo.findByCategoryAndSortOrderGreaterThanEqual(category, fromSortOrder);
        valuesToAdjust.forEach(ca -> ca.setSortOrder(ca.getSortOrder() + 1));
    }
}

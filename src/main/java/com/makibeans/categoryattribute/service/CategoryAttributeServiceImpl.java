// language: java
package com.makibeans.categoryattribute.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.categoryattribute.dto.CategoryAttributeRequestDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeUpdateDTO;
import com.makibeans.categoryattribute.dto.CategoryAttributeUsageDTO;
import com.makibeans.categoryattribute.mapper.CategoryAttributeMapper;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.productattribute.repository.ProductAttributeRepository;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.attribute.model.Attribute;
import com.makibeans.category.model.Category;
import com.makibeans.attribute.service.AttributeService;
import com.makibeans.category.service.CategoryService;
import com.makibeans.web.exceptions.ResourceInUseException;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    @Override // LEGACY METHOD REMOVE WHEN POSSIBLE
    @Transactional(readOnly = true)
    public CategoryAttribute getById(Long id) {
        return repo.findByIdAndDeletedFalse(id).orElseThrow(() -> new ResourceNotFoundException("CategoryAttribute with id " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryAttribute getByCategoryIdAndAttributeIdAndDeletedFalse(Long categoryId, Long attributeId) {
        return repo.findByCategoryIdAndAttributeIdAndDeletedFalse(categoryId, attributeId).orElseThrow(() -> new ResourceNotFoundException("No active link exists between category id " + categoryId + " and attribute id " + attributeId + "."));
    }

    @Override
    @Transactional
    public CategoryAttribute getByCategoryIdAndAttributeIdIncludingDeleted(Long categoryId, Long attributeId) {
        return repo.findByCategoryIdAndAttributeId(categoryId, attributeId).orElseThrow(() -> new ResourceNotFoundException("No link exists between category id " + categoryId + " and attribute id " + attributeId + "."));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttribute> getAllByCategoryId(Long categoryId) {
        return repo.findByCategoryIdAndDeletedFalseOrderBySortOrderAsc(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryAttribute> getAllByCategoryIdIncludingDeleted(Long categoryId) {
        return repo.findByCategoryIdOrderBySortOrderAsc(categoryId);
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryAttributeUsageDTO summarizeUsage(Long categoryId, Long attributeId) {
        CategoryAttribute ca = this.getByCategoryIdAndAttributeIdIncludingDeleted(categoryId, attributeId);
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
    public CategoryAttribute add(Long categoryId, @Valid CategoryAttributeRequestDTO dto) {
        Category category = categoryService.getById(categoryId);
        Attribute attribute = attributeService.getById(dto.getAttributeId());

        int maxSortOrder = repo.findMaxSortOrderByCategoryAndDeletedFalse(category).orElse(-1);

        //Check for existing link
        Optional<CategoryAttribute> existingOpt = repo.findByCategoryIdAndAttributeId(categoryId, attribute.getId());

        if (existingOpt.isPresent()) {
            CategoryAttribute existing = existingOpt.get();

            // if not deleted, throw duplicate exception
            if (!existing.isDeleted()) {
                throw new DuplicateResourceException(
                        "Attribute '" + attribute.getName() + "' is already associated with category '" + category.getName() + "'.");
            }

            // restore
            existing.setDeleted(false);
            existing.setDeletedReason(null);
            existing.setSortOrder(maxSortOrder + 1);
            existing.setRequired(dto.getRequired());
            existing.setFilterable(dto.getFilterable());
            return existing;
        }

        // create new link
        CategoryAttribute created = CategoryAttribute.builder()
                .category(category)
                .attribute(attribute)
                .sortOrder(maxSortOrder + 1)
                .required(dto.getRequired())
                .filterable(dto.getFilterable())
                .build();

        return repo.save(created);
    }

    /**
     * Replaces all CategoryAttribute associations for a given category.
     * Existing associations not in the new list are soft-deleted.
     * Associations in the new list that already exist are updated.
     * New associations are created as needed.
     */
    @Override
    @Transactional
    public List<CategoryAttribute> replaceAll(Long categoryId, List<CategoryAttributeRequestDTO> body) {
        Category category = categoryService.getById(categoryId);

        Set<Long> requestedAttributeIds = body.stream()
                .map(CategoryAttributeRequestDTO::getAttributeId)
                .collect(Collectors.toSet());

        if (requestedAttributeIds.size() != body.size()) {
            throw new DuplicateResourceException("Duplicate attributeId values are not allowed.");
        }

        List<CategoryAttribute> existingLinks = repo.findByCategoryId(categoryId); // include deleted

        // Map ALL links (including deleted) by attributeId
        Map<Long, CategoryAttribute> linksByAttributeId =
                existingLinks.stream()
                        .collect(Collectors.toMap(
                                ca -> ca.getAttribute().getId(),
                                ca -> ca,
                                (a, b) -> a
                        ));

        // Upsert requested links (sortOrder = request order)
        int index = 0;
        for (CategoryAttributeRequestDTO dto : body) {
            Long attributeId = dto.getAttributeId();
            int sortOrder = index++;

            CategoryAttribute existing = linksByAttributeId.get(attributeId);

            if (existing != null) {
                if (existing.isDeleted()) {
                    existing.setDeleted(false);
                    existing.setDeletedReason(null);
                }
                existing.setSortOrder(sortOrder);
                existing.setRequired(dto.getRequired());
            } else {
                Attribute attribute = attributeService.getById(attributeId);

                CategoryAttribute created = CategoryAttribute.builder()
                        .category(category)
                        .attribute(attribute)
                        .sortOrder(sortOrder)
                        .required(dto.getRequired())
                        .build();

                repo.save(created);
            }
        }

        // Soft-delete links not in requested list (only if currently active)
        for (CategoryAttribute link : existingLinks) {
            Long attributeId = link.getAttribute().getId();

            if (!link.isDeleted() && !requestedAttributeIds.contains(attributeId)) {
                assertNotInUse(link);
                link.setDeleted(true);
                link.setDeletedReason(DeleteReason.REPLACED);
            }
        }

        return repo.findByCategoryIdAndDeletedFalseOrderBySortOrderAsc(categoryId);
    }

    /**
     * Updates an existing CategoryAttribute.
     */
    @Override
    @Transactional
    public CategoryAttribute update(Long categoryId, Long attributeId, @Valid CategoryAttributeUpdateDTO updateDTO) {
        CategoryAttribute categoryAttribute = getByCategoryIdAndAttributeIdAndDeletedFalse(categoryId, attributeId);
        mapper.updateEntityFromDTO(updateDTO, categoryAttribute);

        return categoryAttribute;
    }

    /**
     * Deletes a CategoryAttribute association.
     * Throws ResourceInUseException if the association is still in use.
     */
    @Override
    @Transactional
    public void delete(Long categoryId, Long attributeId) {
        CategoryAttribute categoryAttribute = getByCategoryIdAndAttributeIdAndDeletedFalse(categoryId, attributeId);
        if (categoryAttribute.isDeleted()) return;
        assertNotInUse(categoryAttribute);
        categoryAttribute.setDeleted(true);
        categoryAttribute.setDeletedReason(DeleteReason.ADMIN_DELETED);
    }

    @Override
    @Transactional
    public void deleteAllByCategory(Category category) {
        if (productAttributeRepository.existsByCategoryAttributeCategoryAndDeletedFalse(category)) {
            throw new ResourceInUseException(
                    "Cannot delete category attributes for category '" + category.getName() +
                            "' because they are still used by one or more products.");
        }
        List<CategoryAttribute> categoryAttributes = repo.findByCategoryIdAndDeletedFalseOrderBySortOrderAsc(category.getId());
        categoryAttributes.forEach(ca -> {
            ca.setDeleted(true);
            ca.setDeletedReason(DeleteReason.CATEGORY_DELETED);
        });
    }

    @Override
    @Transactional
    public void restoreAllByCategory(Category category, DeleteReason reason) throws BadRequestException {
        if (category == null) {
            throw new IllegalArgumentException("Category cannot be null.");
        }
        assertCategoryNotDeleted(category);

        List<CategoryAttribute> attributes = repo.findByCategoryIdAndDeletedTrueAndDeletedReasonEquals(category.getId(), reason);
        attributes.forEach(ca -> {
            assertUniqueLink(category, ca.getAttribute());
            ca.setDeleted(false);
            ca.setDeletedReason(null);
        });
    }


    // -------------------------------------------------------------------------
    // OTHER
    // -------------------------------------------------------------------------

    /**
     * Retrieves a CategoryAttribute by its ID or throws ResourceNotFoundException.
     */
    private void assertUniqueLink(Category category, Attribute attribute) {
        boolean exists = repo.existsByCategoryAndAttributeAndDeletedFalse(category, attribute);
        if (exists) {
            throw new DuplicateResourceException("Attribute '" + attribute.getName() + "' is already associated with category '" + category.getName() + "'.");
        }
    }


    /**
     * Asserts that the CategoryAttribute is not in use by any products.
     * Throws ResourceInUseException if it is in use.
     */
    private void assertNotInUse(CategoryAttribute ca) {
        boolean inUse = productAttributeRepository.existsByCategoryAttributeAndDeletedFalse(ca);
        if (inUse) {
            throw new ResourceInUseException(
                    "Cannot remove attribute '" + ca.getAttribute().getName() +
                            "' from category '" + ca.getCategory().getName() +
                            "' because it is still used by one or more products.");
        }
    }

    private void assertCategoryNotDeleted(Category category) throws BadRequestException {
        if (category.isDeleted()) {
            throw new BadRequestException("Cannot restore CategoryAttribute because the associated category '" + category.getName() + "' is deleted.");
        }
    }
}

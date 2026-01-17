package com.makibeans.category.service;

import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.attributevalue.repository.AttributeValueRepository;
import com.makibeans.category.filter.dto.*;
import com.makibeans.category.model.Category;
import com.makibeans.category.repository.CategoryRepository;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.categoryattribute.repository.CategoryAttributeRepository;
import com.makibeans.productvariant.repository.ProductVariantRepository;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class CategoryFilterServiceImpl implements CategoryFilterService {

    private final CategoryRepository categoryRepository;
    private final CategoryAttributeRepository categoryAttributeRepository;
    private final AttributeValueRepository attributeValueRepository;
    private final ProductVariantRepository productVariantRepository;

    public CategoryFilterServiceImpl(
            CategoryRepository categoryRepository,
            CategoryAttributeRepository categoryAttributeRepository,
            AttributeValueRepository attributeValueRepository,
            ProductVariantRepository productVariantRepository
    ) {
        this.categoryRepository = categoryRepository;
        this.categoryAttributeRepository = categoryAttributeRepository;
        this.attributeValueRepository = attributeValueRepository;
        this.productVariantRepository = productVariantRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryFilterDefinitionsResponseDTO getCategoryFilterDefinitions(Long categoryId) {
        Category category = getCategoryByIdOrThrow(categoryId);

        PriceFilterDefinitionDTO priceFilter = getPriceFilterDefinition(categoryId);

        List<AttributeFilterDefinitionDTO> attributeFilters = getAttributeFilterDefinitions(categoryId);
        List<SizeFilterDefinitionDTO> sizeFilters = getSizeFilterDefinitions(categoryId);

        boolean inStockAvailable =
                productVariantRepository.existsByProductCategoryIdAndDeletedFalseAndStockGreaterThan(categoryId, 0L);

        ProductFilterDefinitionsDTO productFilters = new ProductFilterDefinitionsDTO(
                priceFilter,
                sizeFilters,
                inStockAvailable
        );

        return new CategoryFilterDefinitionsResponseDTO(
                category.getId(),
                category.getName(),
                category.getSlug(),
                attributeFilters,
                productFilters
        );
    }

    private PriceFilterDefinitionDTO getPriceFilterDefinition(Long categoryId) {
        Long min = productVariantRepository.findMinPriceInCentsByCategoryIdAndDeletedFalse(categoryId);
        Long max = productVariantRepository.findMaxPriceInCentsByCategoryIdAndDeletedFalse(categoryId);

        if (min == null || max == null) return null;
        return new PriceFilterDefinitionDTO(min, max);
    }

    private List<SizeFilterDefinitionDTO> getSizeFilterDefinitions(Long categoryId) {
        return productVariantRepository.findDistinctSizesByCategoryIdAndDeletedFalse(categoryId)
                .stream()
                .map(size -> new SizeFilterDefinitionDTO(size.getId(), size.getName(), size.getSlug()))
                .toList();
    }

    private List<AttributeFilterDefinitionDTO> getAttributeFilterDefinitions(Long categoryId) {

        List<CategoryAttribute> links =
                categoryAttributeRepository.findByCategoryIdAndDeletedFalseOrderBySortOrderAsc(categoryId);

        if (links.isEmpty()) return List.of();

        Set<Long> libraryBackedAttributeIds = links.stream()
                .filter(link -> link.getAttribute().getInputType().isLibraryBacked())
                .map(link -> link.getAttribute().getId())
                .collect(Collectors.toSet());

        Map<Long, List<AttributeValueFilterOptionDTO>> valuesByAttributeId =
                libraryBackedAttributeIds.isEmpty()
                        ? Map.of()
                        : attributeValueRepository.findByAttributeIdInAndDeletedFalse(libraryBackedAttributeIds)
                        .stream()
                        .collect(Collectors.groupingBy(
                                v -> v.getAttribute().getId(),
                                Collectors.mapping(this::toAttributeValueFilterOptionDTO, Collectors.toList())
                        ));

        return links.stream()
                .map(link -> toAttributeFilterDefinitionDTO(link, valuesByAttributeId))
                .toList();
    }

    private AttributeValueFilterOptionDTO toAttributeValueFilterOptionDTO(AttributeValue v) {
        return new AttributeValueFilterOptionDTO(v.getId(), v.getValueAsString(), v.getSlug());
    }

    private AttributeFilterDefinitionDTO toAttributeFilterDefinitionDTO(
            CategoryAttribute link,
            Map<Long, List<AttributeValueFilterOptionDTO>> valuesByAttributeId
    ) {
        Long attributeId = link.getAttribute().getId();
        boolean libraryBacked = link.getAttribute().getInputType().isLibraryBacked();

        return new AttributeFilterDefinitionDTO(
                attributeId,
                link.getAttribute().getName(),
                link.getAttribute().getSlug(),
                link.getRequired(),
                link.getSortOrder(),
                link.getAttribute().getDataType(),
                link.getAttribute().getInputType(),
                libraryBacked,
                libraryBacked ? valuesByAttributeId.getOrDefault(attributeId, List.of()) : List.of()
        );
    }

    private Category getCategoryByIdOrThrow(Long categoryId) {
        return categoryRepository.findByIdAndDeletedFalse(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found with id: " + categoryId));
    }
}

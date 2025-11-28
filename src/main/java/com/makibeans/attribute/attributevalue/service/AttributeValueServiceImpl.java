package com.makibeans.attribute.attributevalue.service;

import com.makibeans.attribute.attribute.model.AttributeDataType;
import com.makibeans.attribute.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attribute.attributevalue.mapper.AttributeValueMapper;
import com.makibeans.attribute.attributevalue.repository.AttributeValueRepository;
import com.makibeans.attribute.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.attribute.attributevalue.util.AttributeValueParser;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceInUseException;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.attribute.productattributevalue.repository.ProductAttributeValueRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.attribute.attributevalue.filter.AttributeValueFilter;
import com.makibeans.attribute.attribute.service.AttributeService;
import com.makibeans.common.service.CrudService;
import com.makibeans.common.util.TextUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Service class for managing AttributeValues.
 */

@Service
public class AttributeValueServiceImpl implements CrudService<AttributeValue, Long>, AttributeValueService {

    private final AttributeValueRepository repo;
    private final AttributeService attributeService;
    private final ProductAttributeValueRepository productAttributeValueRepository;
    private final AttributeValueMapper mapper;


    @Autowired
    public AttributeValueServiceImpl(
            AttributeValueRepository repo,
            AttributeService attributeService,
            ProductAttributeValueRepository productAttributeValueRepository,
            AttributeValueMapper mapper
    ) {
        this.repo = repo;
        this.attributeService = attributeService;
        this.productAttributeValueRepository = productAttributeValueRepository;
        this.mapper = mapper;
    }

    @Override
    public JpaRepository<AttributeValue, Long> repo() {
        return this.repo;
    }

    @Transactional(readOnly = true)
    public AttributeValue getById(Long id) {
        return getOrThrow(id);
    }

    @Transactional
    public Page<AttributeValue> search(SearchRequest<AttributeValueFilter> req) {
        Specification<AttributeValue> spec =
                SpecificationFactory.fromRequest(req, AttributeValueFilter.class);

        Sort sort = new SortResolver(AttributeValueFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    @Transactional
    public AttributeValue create(AttributeValueRequestDTO requestDTO) throws BadRequestException {
        Attribute a = attributeService.getOrThrow(requestDTO.getAttributeId());
        AttributeDataType type = a.getDataType();

        String normalizedValue = TextUtils.normalizeText(requestDTO.getRawValue());

        if (exists(normalizedValue, type, a)) {
            throw new DuplicateResourceException("Attribute value '" + normalizedValue + "' already exists for attribute '" + a.getName() + "'.");
        }

        AttributeValue av = new AttributeValue();

        av.setAttribute(a);
        applyRawValue(av, a.getDataType(), normalizedValue);
        av.setSlug(TextUtils.toSlug(normalizedValue));
        av.setSortOrder(resolveSortOrder(a, requestDTO.getSortOrder()));

        return repo.save(av);
    }

    @Transactional
    public AttributeValue update(Long id, AttributeValueUpdateDTO updateDTO) throws BadRequestException, DuplicateResourceException {
        AttributeValue av = getOrThrow(id);

        Attribute attribute = av.getAttribute();
        AttributeDataType type = attribute.getDataType();

        if (updateDTO.getRawValue() != null) {
            String newNormalizedValue = TextUtils.normalizeText(updateDTO.getRawValue());

            if (existsAndIdNot(newNormalizedValue, type, attribute, av.getId())) {
                throw new DuplicateResourceException("Attribute value '" + newNormalizedValue + "' already exists for attribute '" + attribute.getName() + "'.");
            }

            applyRawValue(av, type, newNormalizedValue);
            av.setSlug(TextUtils.toSlug(newNormalizedValue));
        }

        //sort order logic
        Integer currentSortOrder = av.getSortOrder();
        Integer requestedSortOrder = updateDTO.getSortOrder();
        boolean sortOrderChanged = requestedSortOrder != null && !requestedSortOrder.equals(currentSortOrder);

        if (sortOrderChanged) {
            int newSortOrder = resolveSortOrder(attribute, updateDTO.getSortOrder());
            av.setSortOrder(newSortOrder);
        }

        mapper.updateEntityFromDTO(updateDTO, av); //ignores value, slug

        return av;
    }

    @Transactional
    public void delete(Long id) {

        boolean inUseByProductAttributeValues = productAttributeValueRepository.existsByAttributeValueId(id);

        if (inUseByProductAttributeValues) {
            throw new ResourceInUseException("Attribute value with id '" + id + "' is in use and cannot be deleted.");
        }
        hardDelete(id);
    }

    private void applyRawValue(AttributeValue v, AttributeDataType type, String normalizedValue) throws BadRequestException {

        if (normalizedValue == null || normalizedValue.isBlank()) {
            throw new BadRequestException("Attribute value cannot be empty.");
        }

        switch (type) {
            case STRING -> v.setStringValue(TextUtils.normalizeText(normalizedValue));
            case NUMERIC -> v.setNumericValue(AttributeValueParser.parseNumeric(normalizedValue));
            case BOOLEAN -> v.setBooleanValue(AttributeValueParser.parseBoolean(normalizedValue));
            case DATE -> v.setDateValue(AttributeValueParser.parseDate(normalizedValue));
            case DATETIME -> v.setDateTimeValue(AttributeValueParser.parseDateTime(normalizedValue));
        }
    }

    private boolean exists(String value, AttributeDataType type, Attribute attribute) throws IllegalArgumentException {
        return existsInternal(value, type, attribute, null);
    }

    private boolean existsAndIdNot(String value, AttributeDataType type, Attribute attribute, Long excludeId) throws IllegalArgumentException {
        return existsInternal(value, type, attribute, excludeId);
    }

    private boolean existsInternal(String value, AttributeDataType type, Attribute attribute, Long excludeId) throws IllegalArgumentException {
        return switch (type) {
            case STRING -> excludeId == null
                    ? repo.existsByAttributeAndStringValueIgnoreCase(attribute, value)
                    : repo.existsByAttributeAndStringValueIgnoreCaseAndIdNot(attribute, value, excludeId);
            case NUMERIC -> excludeId == null
                    ? repo.existsByAttributeAndNumericValue(attribute, AttributeValueParser.parseNumeric(value))
                    : repo.existsByAttributeAndNumericValueAndIdNot(attribute, AttributeValueParser.parseNumeric(value), excludeId);
            case BOOLEAN -> excludeId == null
                    ? repo.existsByAttributeAndBooleanValue(attribute, AttributeValueParser.parseBoolean(value))
                    : repo.existsByAttributeAndBooleanValueAndIdNot(attribute, AttributeValueParser.parseBoolean(value), excludeId);
            case DATE -> excludeId == null
                    ? repo.existsByAttributeAndDateValue(attribute, AttributeValueParser.parseDate(value))
                    : repo.existsByAttributeAndDateValueAndIdNot(attribute, AttributeValueParser.parseDate(value), excludeId);
            case DATETIME -> excludeId == null
                    ? repo.existsByAttributeAndDateTimeValue(attribute, AttributeValueParser.parseDateTime(value))
                    : repo.existsByAttributeAndDateTimeValueAndIdNot(attribute, AttributeValueParser.parseDateTime(value), excludeId);
        };
    }

    private int resolveSortOrder(Attribute a, Integer requestedSortOrder) {

        int maxSortOrder = repo.findMaxSortOrderByAttribute(a).orElse(-1);
        int nextSortOrder = maxSortOrder + 1;

        boolean validSortOrderRequest =
                requestedSortOrder != null &&
                        requestedSortOrder >= 0 &&
                        requestedSortOrder <= maxSortOrder;

        if (validSortOrderRequest) {
            adjustSortOrdersForInsert(a, requestedSortOrder);
            return requestedSortOrder;
        } else {
            return nextSortOrder;
        }
    }

    private void adjustSortOrdersForInsert(Attribute attribute, int fromSortOrder) {
        List<AttributeValue> valuesToAdjust = repo.findByAttributeAndSortOrderGreaterThanEqual(attribute, fromSortOrder);
        valuesToAdjust.forEach(av -> av.setSortOrder(av.getSortOrder() + 1));
    }
}

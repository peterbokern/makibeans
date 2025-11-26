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
import com.makibeans.util.TextUtils;
import jakarta.validation.ValidationException;
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
            AttributeService attributeService, ProductAttributeValueRepository productAttributeValueRepository,
            AttributeValueMapper mapper) {
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

        return repo.save(av);
    }

    @Transactional
    public AttributeValue update(Long id, AttributeValueUpdateDTO updateDTO) throws BadRequestException, DuplicateResourceException {
        AttributeValue av = getOrThrow(id);

        Attribute attribute = av.getAttribute();
        AttributeDataType type = attribute.getDataType();

        String newNormalizedValue = TextUtils.normalizeText(updateDTO.getRawValue());

        if (exists(newNormalizedValue, type, attribute)) {
            throw new DuplicateResourceException("Attribute value '" + newNormalizedValue + "' already exists for attribute '" + attribute.getName() + "'.");
        }
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
        return switch (type) {
            case STRING -> repo.existsByAttributeAndStringValueIgnoreCase(attribute, value);
            case NUMERIC -> repo.existsByAttributeAndNumericValue(attribute, AttributeValueParser.parseNumeric(value));
            case BOOLEAN -> repo.existsByAttributeAndBooleanValue(attribute, AttributeValueParser.parseBoolean(value));
            case DATE -> repo.existsByAttributeAndDateValue(attribute, AttributeValueParser.parseDate(value));
            case DATETIME ->
                    repo.existsByAttributeAndDateTimeValue(attribute, AttributeValueParser.parseDateTime(value));
        };
    }
}
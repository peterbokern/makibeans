package com.makibeans.attribute.attributevalue.service;

import com.makibeans.attribute.attribute.model.AttributeDataType;
import com.makibeans.attribute.attributevalue.dto.AttributeValueUpdateDTO;
import com.makibeans.attribute.attributevalue.dto.AttributeValueUsageDTO;
import com.makibeans.attribute.attributevalue.filter.AttributeValueAdminFilter;
import com.makibeans.attribute.attributevalue.filter.AttributeValuePublicFilter;
import com.makibeans.attribute.attributevalue.mapper.AttributeValueMapper;
import com.makibeans.attribute.attributevalue.repository.AttributeValueRepository;
import com.makibeans.attribute.attributevalue.dto.AttributeValueRequestDTO;
import com.makibeans.attribute.attributevalue.util.AttributeValueParser;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.web.exceptions.ResourceInUseException;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.attribute.attribute.service.AttributeService;
import com.makibeans.common.util.TextUtils;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Domain service for managing {@link AttributeValue} entities.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>Lookup and search of attribute values via the generic search infrastructure.</li>
 *   <li>Creation and update of values with type-safe parsing based on {@link AttributeDataType}.</li>
 *   <li>Uniqueness checks per attribute and data type.</li>
 *   <li>Soft delete / restore semantics.</li>
 *   <li>Sort-order handling within a single {@link Attribute}.</li>
 * </ul>
 */
@Service
public class AttributeValueServiceImpl implements AttributeValueService {

    private final AttributeValueRepository repo;
    private final AttributeService attributeService;
    private final AttributeValueMapper mapper;
    private final AttributeValueUsageChecker usageChecker;

    @Autowired
    public AttributeValueServiceImpl(
            AttributeValueRepository repo,
            AttributeService attributeService,
            AttributeValueMapper mapper, AttributeValueUsageChecker usageChecker
    ) {
        this.repo = repo;
        this.attributeService = attributeService;
        this.mapper = mapper;
        this.usageChecker = usageChecker;
    }

    /**
     * Returns an {@link AttributeValue} by id or throws if it does not exist.
     *
     * @param id the database identifier of the attribute value
     * @return the resolved {@link AttributeValue}
     * @throws ResourceNotFoundException if no value with the given id exists
     */
    @Override
    @Transactional(readOnly = true)
    public AttributeValue getById(Long id) {
        return repo.findByIdAndDeletedFalse(id).orElseThrow(() ->
                new ResourceNotFoundException("Attribute value with ID " + id + " not found."));
    }

    @Override
    @Transactional
    public AttributeValue getByIdIncludingDeleted(Long id) {
        return repo.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Attribute value with ID " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttributeValue> searchPublic(SearchRequest<AttributeValuePublicFilter> req) {
        return search(req, AttributeValuePublicFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<AttributeValue> searchAdmin(SearchRequest<AttributeValueAdminFilter> req) {
        return search(req, AttributeValueAdminFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public <F> Page<AttributeValue> search(SearchRequest<F> req, Class<F> filterClass) {
        Specification<AttributeValue> spec =
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

    /**
     * Creates a new {@link AttributeValue} for the given attribute.
     * <p>
     * Steps:
     * <ol>
     *   <li>Resolve {@link Attribute} from the id contained in the request.</li>
     *   <li>Normalise the raw value and ensure uniqueness for the attribute + data type.</li>
     *   <li>Parse and apply the type-specific value field (string/number/boolean/date/... ).</li>
     *   <li>Generate slug and sort-order.</li>
     *   <li>Persist and return the entity.</li>
     * </ol>
     *
     * @param requestDTO value creation request
     * @return the persisted {@link AttributeValue}
     * @throws BadRequestException        if the raw value cannot be parsed or is empty
     * @throws DuplicateResourceException if the value already exists for the given attribute
     */
    @Override
    @Transactional
    public AttributeValue create(AttributeValueRequestDTO requestDTO) throws BadRequestException {
        Attribute attribute = attributeService.getById(requestDTO.getAttributeId());
        AttributeDataType type = attribute.getDataType();

        String rawValue = requestDTO.getRawValue();
        String trimmedValue = rawValue.trim();
        String normalizedValue = normalize(trimmedValue);
        String slug = TextUtils.toSlug(normalizedValue);
        assertUniqueSlugForCreate(slug, attribute);
        int sortOrder = resolveSortOrder(attribute, requestDTO.getSortOrder());

        //initialize  entity
        AttributeValue value = new AttributeValue();

        value.setAttribute(attribute);
        applyRawValue(value, type, trimmedValue);
        value.setSlug(slug);
        value.setSortOrder(sortOrder);

        return repo.save(value);
    }

    /**
     * Updates an existing {@link AttributeValue}.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>If a new raw value is provided, it is normalised, checked for uniqueness,
     *       parsed and stored in the correct typed column, and the slug is updated.</li>
     *   <li>If the sort-order changes, the surrounding values for the same attribute are
     *       shifted accordingly to keep a dense order.</li>
     *   <li>All other updatable fields are delegated to {@link AttributeValueMapper}.</li>
     * </ul>
     *
     * @param id        id of the existing value to update
     * @param updateDTO data to apply
     * @return the updated (but not yet reloaded) {@link AttributeValue}
     * @throws BadRequestException        if the new raw value is invalid
     * @throws DuplicateResourceException if the new value would violate uniqueness
     */
    @Override
    @Transactional
    public AttributeValue update(Long id, AttributeValueUpdateDTO updateDTO) throws BadRequestException, DuplicateResourceException {
        AttributeValue av = this.getById(id);

        Attribute attribute = av.getAttribute();
        AttributeDataType type = attribute.getDataType();

        //update raw value, slug if present and sort order if changed
        updateRawValueIfPresent(av, type, updateDTO);
        updateSortOrderIfChanged(av, attribute, updateDTO);

        // update remaining scalar fields (mapper is configured to ignore value + slug)
        mapper.updateEntityFromDTO(updateDTO, av);

        return av;
    }

    /**
     * Performs a soft delete by marking the value as deleted.
     *
     * @param id id of the value to mark as deleted
     * @throws ResourceNotFoundException if no entity with the given id exists
     */
    @Override
    @Transactional
    public void delete(Long id) {
        AttributeValue value = getById(id);
        if (Boolean.TRUE.equals(value.isDeleted())) return;
        assertNotInUse(value); // prevent deletion if in use
        value.setDeleted(true);
    }

    /**
     * Restores a previously soft-deleted {@link AttributeValue}.
     * <p>
     * Before restoring, the method checks that the effective value for the
     * associated attribute is still unique.
     *
     * @param id id of the value to restore
     * @return the restored {@link AttributeValue}
     * @throws DuplicateResourceException if restoring would violate uniqueness
     */

    @Override
    @Transactional
    public AttributeValue restore(Long id) throws DuplicateResourceException, BadRequestException {
        AttributeValue value = getByIdIncludingDeleted(id);
        assertDeleted(value);
        Attribute attribute = value.getAttribute();
        String slug = value.getSlug();
        assertUniqueSlugForUpdate(slug, attribute, id);
        value.setDeleted(false);
        return value;
    }

    @Override
    @Transactional(readOnly = true)
    public AttributeValueUsageDTO summarizeAttributeValueUsage(Long attributeValueId) {
        AttributeValue value = getById(attributeValueId);
        return usageChecker.summarizeUsage(value);
    }

    /**
     * Parses and assigns the normalised raw value to the correct typed column on the entity.
     *
     * @param target   entity to mutate
     * @param type     attribute value data type
     * @param rawValue already-normalised raw value (not blank)
     * @throws BadRequestException if the value is blank or cannot be parsed for the given type
     */
    private void applyRawValue(AttributeValue target,
                               AttributeDataType type,
                               String rawValue) throws BadRequestException {

        if (rawValue == null) throw new BadRequestException("Attribute value cannot be empty.");

        String trimmedValue = rawValue.trim();

        if (trimmedValue.isBlank()) throw new BadRequestException("Attribute value cannot be empty.");

        String normalizedValue = normalize(trimmedValue);

        switch (type) {
            case STRING -> {target.setStringValue(trimmedValue);
            }
            case NUMBER -> target.setNumericValue(
                    AttributeValueParser.parseNumeric(normalizedValue)
            );
            case BOOLEAN -> target.setBooleanValue(
                    AttributeValueParser.parseBoolean(normalizedValue)
            );
            case DATE -> target.setDateValue(
                    AttributeValueParser.parseDate(normalizedValue)
            );
            case DATETIME -> target.setDateTimeValue(
                    AttributeValueParser.parseDateTime(normalizedValue)
            );
            default -> throw new BadRequestException(
                    "Unsupported attribute data type: " + type
            );
        }
    }

    /**
     * Convenience wrapper for normalising free-text values.
     *
     * @param rawValue value as provided by the client (may be null/blank)
     * @return normalised representation, never {@code null}
     */
    private String normalize(String rawValue) {
        return TextUtils.normalizeText(rawValue);
    }

    /**
     * Ensures that no existing (non-deleted) value for the given attribute
     * has the same slug.
     *
     * @param slug      slug to check
     * @param attribute owning attribute
     * @throws DuplicateResourceException if a value with the same slug already exists
     */
    private void assertUniqueSlugForCreate(String slug, Attribute attribute) {
        AttributeValue existing = repo.findBySlugAndAttributeAndDeletedFalse(slug, attribute);
        if (existing != null) {
            throw new DuplicateResourceException(
                    "attribute value: " + existing.getValueAsString() + " already exists."
            );
        }
    }

    /**
     * Ensures that no existing (non-deleted) value for the given attribute
     * has the same slug, excluding the value with the given id.
     *
     * @param slug      slug to check
     * @param attribute owning attribute
     * @param excludeId id to exclude from the check
     * @throws DuplicateResourceException if a value with the same slug already exists
     */
    private void assertUniqueSlugForUpdate(String slug, Attribute attribute, Long excludeId) {
        AttributeValue existing = repo.findBySlugAndAttributeAndIdNotAndDeletedFalse(slug, attribute, excludeId);
        if (existing != null) {
            throw new DuplicateResourceException(
                    "Attribute value: " + existing.getValueAsString() + " already exists."
            );
        }
    }

    /**
     * Asserts that the given {@link AttributeValue} is marked as deleted.
     *
     * @param value entity to check
     * @throws BadRequestException if the entity is not deleted
     */
    private void assertDeleted(AttributeValue value) throws BadRequestException {
        if (!Boolean.TRUE.equals(value.isDeleted())) {
            throw new BadRequestException("Attribute value + " + value.getValueAsString() + " is not deleted.");
        }
    }

    /**
     * Asserts that the given {@link AttributeValue} is not in use.
     *
     * @param value entity to check
     */
    private void assertNotInUse(AttributeValue value) {
        boolean inUse = usageChecker.isInUse(value);
        if (inUse) {
            String details = usageChecker.getUsageDetails(value);
            throw new ResourceInUseException(
                    "Attribute value: " + value.getValueAsString() + " is in use and cannot be deleted. " + details
            );
        }
    }

    /**
     * Updates the raw value and slug of an existing {@link AttributeValue}
     * if a new raw value is provided in the DTO.
     *
     * @param value entity to update
     * @param type  attribute data type
     * @param dto   update DTO
     * @throws BadRequestException if the new raw value is invalid
     */
    private void updateRawValueIfPresent(AttributeValue value,
                                         AttributeDataType type,
                                         AttributeValueUpdateDTO dto) throws BadRequestException {

        if (dto.getRawValue() == null) return;

        Attribute attribute = value.getAttribute();
        Long id = value.getId();

        String newTrimmedRawValue = dto.getRawValue().trim();
        String newNormalizedValue = normalize(newTrimmedRawValue);
        String slug = TextUtils.toSlug(newNormalizedValue);

        assertUniqueSlugForUpdate(slug, attribute, id);

        applyRawValue(value, type, newTrimmedRawValue);
        value.setSlug(slug);
    }

    /**
     * Updates the sort-order of an existing {@link AttributeValue}
     * if it has changed in the DTO.
     *
     * @param av        entity to update
     * @param attribute owning attribute
     * @param dto       update DTO
     */
    private void updateSortOrderIfChanged(
            AttributeValue av,
            Attribute attribute,
            AttributeValueUpdateDTO dto) {

        Integer currentSortOrder = av.getSortOrder();
        Integer requestedSortOrder = dto.getSortOrder();

        if (requestedSortOrder == null || requestedSortOrder.equals(currentSortOrder)) {
            return;
        }

        int newSortOrder = resolveSortOrder(attribute, requestedSortOrder);
        av.setSortOrder(newSortOrder);
    }

    /**
     * Resolves the final sort-order for a new or moved value within a given attribute.
     * <p>
     * If a valid explicit sort-order is requested (within [0, max]), existing
     * values with sort-order {@code >= requestedSortOrder} are shifted up by one
     * and the requested position is used. Otherwise, the value is appended at the
     * end (max + 1).
     *
     * @param attribute          the owning {@link Attribute}
     * @param requestedSortOrder the desired sort-order (may be {@code null})
     * @return the sort-order actually applied
     */
    private int resolveSortOrder(Attribute attribute, Integer requestedSortOrder) {

        int maxSortOrder = repo.findMaxSortOrderByAttribute(attribute).orElse(-1);

        if (isValidSortOrderRequest(requestedSortOrder, maxSortOrder)) {
            adjustSortOrdersForInsert(attribute, requestedSortOrder);
            return requestedSortOrder;
        } else {
            return maxSortOrder + 1;
        }
    }

    private boolean isValidSortOrderRequest(Integer requestedSortOrder, int maxSortOrder) {
        return requestedSortOrder != null &&
                requestedSortOrder >= 0 &&
                requestedSortOrder <= maxSortOrder;
    }

    /**
     * Shifts all {@link AttributeValue} instances for the given attribute with
     * {@code sortOrder >= fromSortOrder} up by one to make room for a new
     * value at {@code fromSortOrder}.
     */
    private void adjustSortOrdersForInsert(Attribute attribute, int fromSortOrder) {
        List<AttributeValue> valuesToAdjust = repo.findByAttributeAndSortOrderGreaterThanEqual(attribute, fromSortOrder);
        valuesToAdjust.forEach(av -> av.setSortOrder(av.getSortOrder() + 1));
    }
}

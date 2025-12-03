package com.makibeans.attribute.attribute.service;

import com.makibeans.attribute.attribute.dto.AttributeRequestDTO;
import com.makibeans.attribute.attribute.dto.AttributeUpdateDTO;
import com.makibeans.attribute.attribute.dto.AttributeUsageDTO;
import com.makibeans.attribute.attribute.filter.AttributeFilter;
import com.makibeans.attribute.attribute.mapper.AttributeMapper;
import com.makibeans.attribute.attribute.repository.AttributeRepository;
import com.makibeans.common.util.TextUtils;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.web.exceptions.ResourceInUseException;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import jakarta.validation.Valid;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * Application service for managing {@link Attribute} entities.
 * <p>
 * Responsibilities:
 * <ul>
 *   <li>CRUD-style operations for attributes (create, update, soft delete, restore).</li>
 *   <li>Paged, filterable search using the generic search infrastructure.</li>
 *   <li>Enforcing name uniqueness (using normalised names via {@link TextUtils}).</li>
 * </ul>
 */
@Service
public class AttributeServiceImpl implements AttributeService {

    private final AttributeMapper mapper;
    private final AttributeRepository repo;
    private final AttributeUsageChecker usageChecker;

    @Autowired
    public AttributeServiceImpl(AttributeMapper mapper, AttributeRepository repo, AttributeUsageChecker usageChecker) {
        this.mapper = mapper;
        this.repo = repo;
        this.usageChecker = usageChecker;
    }

    /**
     * Retrieves an {@link Attribute} by its id.
     *
     * @param id the identifier of the attribute to retrieve
     * @return the resolved {@link Attribute}
     * @throws ResourceNotFoundException if no attribute exists with the given id
     */
    @Transactional(readOnly = true)
    public Attribute getById(Long id) {
        return repo.findById(id).orElseThrow(
                () -> new ResourceNotFoundException("Attribute with ID " + id + " not found."));
    }

    /**
     * Executes a paged, filtered search for {@link Attribute} entities.
     * <p>
     * The provided {@link SearchRequest} is mapped to a {@link Specification} via
     * {@link SpecificationFactory} using {@link AttributeFilter} metadata and
     * combined with sorting information resolved by {@link SortResolver}.
     *
     * @param req the search request containing filters, paging and sorting options
     * @return a page of matching {@link Attribute} entities
     */
    @Transactional(readOnly = true)
    public Page<Attribute> search(SearchRequest<AttributeFilter> req) {
        Specification<Attribute> spec =
                SpecificationFactory.fromRequest(req, AttributeFilter.class);

        Sort sort = new SortResolver(AttributeFilter.class)
                .resolve(req.getSortBy(), req.getSortDirection());

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(spec, pageable);
    }

    /**
     * Creates a new {@link Attribute}.
     * <p>
     * Steps:
     * <ol>
     *   <li>Normalise the incoming name using {@link TextUtils#normalizeText(String)}.</li>
     *   <li>Ensure that no other attribute already uses this normalised name.</li>
     *   <li>Initialise the entity (name, slug, description, data type, input type).</li>
     *   <li>Persist and return the attribute.</li>
     * </ol>
     *
     * @param dto the creation request DTO
     * @return the persisted {@link Attribute}
     * @throws DuplicateResourceException if an attribute with the same normalised name already exists
     */
    @Transactional
    public Attribute create(AttributeRequestDTO dto) {
        String trimmedName = dto.getName().trim();
        String normalizedName = TextUtils.normalizeText(trimmedName);
        String slug = TextUtils.toSlug(normalizedName);
        assertUniqueSlug(slug);

        Attribute attribute = new Attribute();
        attribute.setName(trimmedName);
        attribute.setSlug(TextUtils.toSlug(normalizedName));
        attribute.setDescription(dto.getDescription().trim());
        attribute.setDataType(dto.getDataType());
        attribute.setInputType(dto.getInputType());

        return repo.save(attribute);
    }

    /**
     * Updates an existing {@link Attribute}.
     * <p>
     * Behaviour:
     * <ul>
     *   <li>If a new name is provided, it is normalised, validated for uniqueness
     *       (excluding the current attribute) and used to update the name and slug.</li>
     *   <li>All other updatable fields are delegated to {@link AttributeMapper}.</li>
     * </ul>
     *
     * @param id  the id of the attribute to update
     * @param dto the update DTO
     * @return the updated {@link Attribute} (managed entity)
     * @throws ResourceNotFoundException  if the attribute does not exist
     * @throws DuplicateResourceException if the (normalised) updated name conflicts with another attribute
     */
    @Transactional
    public Attribute update(Long id, @Valid AttributeUpdateDTO dto) {
        Attribute attribute = getById(id);

        // update name if present in DTO
        updateNameIfPresent(attribute, dto);

        // update other fields (mapper is configured to ignore name & slug)
        mapper.updateEntityFromDTO(dto, attribute);

        return attribute;
    }

    /**
     * Performs a soft delete by marking the attribute as deleted.
     * <p>
     * If the attribute is already marked as deleted, this method is a no-op.
     *
     * @param id the id of the attribute to delete
     * @throws ResourceNotFoundException if the attribute does not exist
     */
    @Transactional
    public void delete(Long id) throws BadRequestException {
        Attribute attribute = getById(id);
        if (Boolean.TRUE.equals(attribute.isDeleted())) {
            return; // already deleted, no-op
        }

        boolean inUse = usageChecker.isInUse(id);

        if (inUse) {
            String details = usageChecker.getUsageDetails(id);
            throw new ResourceInUseException(
                    "Attribute " + "'" + attribute.getName() +"'" + " (ID " + id + ") is in use and cannot be deleted. " + details
            );
        }

        attribute.setDeleted(true);
    }

    /**
     * Checks if an {@link Attribute} is in use by any attribute values,
     * product attributes, or category attributes.
     *
     * @param attributeId the id of the attribute to check
     * @return {@code true} if the attribute is in use; {@code false} otherwise
     */

    @Override
    @Transactional(readOnly = true)
    public AttributeUsageDTO summarizeAttributeUsage(Long attributeId) {
        return usageChecker.summarizeUsage(attributeId);
    }

    /**
     * Restores a previously soft-deleted {@link Attribute}.
     * <p>
     * Checks:
     * <ul>
     *   <li>The attribute must currently be marked as deleted.</li>
     *   <li>The (normalised) current name must not conflict with any other attribute.</li>
     * </ul>
     *
     * @param id the id of the attribute to restore
     * @return the restored {@link Attribute}
     * @throws BadRequestException        if the attribute is not deleted and therefore cannot be restored
     * @throws DuplicateResourceException if restoring would violate name uniqueness
     * @throws ResourceNotFoundException  if the attribute does not exist
     */
    @Override
    @Transactional
    public Attribute restore(Long id) throws BadRequestException {
        Attribute attribute = getById(id);

        if (!Boolean.TRUE.equals(attribute.isDeleted())) {
            throw new BadRequestException(
                    "Attribute with ID " + id + " is not deleted and cannot be restored.");
        }
        String slug = attribute.getSlug();
        assertUniqueSlugAndIdNot(id, slug);
        attribute.setDeleted(false);
        return attribute;
    }

    /**
     * Ensures that no attribute exists with the given normalised name.
     *
     * @param slug normalised attribute name (may be {@code null})
     * @throws DuplicateResourceException if a duplicate is found
     */
    private void assertUniqueSlug(String slug) {
        Attribute existing = repo.findBySlug(slug).orElse(null);
        if (existing != null) {
            throw new DuplicateResourceException(
                    "Attribute with name '" + existing.getName() + "' already exists.");
        }
    }

    /**
     * Ensures that no other attribute (excluding the given id) exists with the
     * given normalised name.
     *
     * @param id   id to exclude from the uniqueness check
     * @param name normalised attribute name (may be {@code null})
     * @throws DuplicateResourceException if a duplicate is found
     */
    private void assertUniqueSlugAndIdNot(Long id, String name) {
        Attribute existing = repo.findBySlugAndIdNot(name, id).orElse(null);
        if (existing != null) {
            throw new DuplicateResourceException(
                    "Attribute with name '" + existing.getName() + "' already exists.");
        }
    }

    /**
     * Updates the attribute's name and slug if a new name is provided in the DTO.
     * <p>
     * The new name is normalised and validated for uniqueness (excluding the
     * current attribute) before being applied.
     *
     * @param attribute the target {@link Attribute} entity
     * @param dto       the update DTO potentially containing a new name
     */
    private void updateNameIfPresent(Attribute attribute, AttributeUpdateDTO dto) {
        if (dto.getName() == null) {
            return;
        }

        String trimmedName = dto.getName().trim();
        String normalizedName = TextUtils.normalizeText(dto.getName());
        String slug = TextUtils.toSlug(normalizedName);
        assertUniqueSlugAndIdNot(attribute.getId(), slug);

        attribute.setName(trimmedName);
        attribute.setSlug(slug);
    }

}

package com.makibeans.service;

import com.makibeans.dto.attributetemplate.AttributeTemplateRequestDTO;
import com.makibeans.dto.attributetemplate.AttributeTemplateResponseDTO;
import com.makibeans.dto.attributetemplate.AttributeTemplateUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.filter.SearchFilter;
import com.makibeans.mapper.AttributeTemplateMapper;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import static com.makibeans.util.UpdateUtils.normalize;
import static com.makibeans.util.UpdateUtils.shouldUpdate;

/**
 * Service class for managing AttributeTemplate entities.
 * Provides methods to perform CRUD operations and search for AttributeTemplates.
 */
@Service
public class AttributeTemplateService extends AbstractCrudService<AttributeTemplate, Long> {

    private final AttributeTemplateRepository attributeTemplateRepository;
    private final AttributeTemplateMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(AttributeTemplateService.class);
    private final ProductAttributeService productAttributeService;

    @Autowired
    public AttributeTemplateService(
            JpaRepository<AttributeTemplate, Long> repository,
            AttributeTemplateRepository attributeTemplateRepository,
            AttributeTemplateMapper mapper, @Lazy ProductAttributeService productAttributeService) {
        super(repository);
        this.attributeTemplateRepository = attributeTemplateRepository;
        this.mapper = mapper;
        this.productAttributeService = productAttributeService;
    }

    /**
     * Retrieves an AttributeTemplate by its unique identifier.
     *
     * @param id the unique identifier of the AttributeTemplate to retrieve.
     * @return the AttributeTemplateResponseDTO representing the found attribute template.
     * @throws IllegalArgumentException  if the provided id is null
     * @throws ResourceNotFoundException if no AttributeTemplate is found with the given id.
     */

    @Transactional(readOnly = true)
    public AttributeTemplateResponseDTO getAttributeTemplateById(Long id) {
        AttributeTemplate attributeTemplate = findById(id);
        return mapper.toResponseDTO(attributeTemplate);
    }

    /**
     * Searches for AttributeTemplates based on the provided filters.
     * The search is performed on the name of the AttributeTemplate.
     *
     * @param searchParams the map containing the search params
     * @return a list of AttributeTemplateResponseDTOs representing the matched attribute templates
     */

    @Transactional(readOnly = true)
    public List<AttributeTemplateResponseDTO> findBySearchQuery(Map<String, String> searchParams) {
        logger.debug("Searching AttributeTemplates with filters: {}", searchParams);

        Map<String, Function<AttributeTemplate, String>> searchFields = Map.of(
                "name", AttributeTemplate::getName);

        Map<String, Comparator<AttributeTemplate>> sortFields = Map.of(
                "id", Comparator.comparing(AttributeTemplate::getId, Comparator.nullsLast(Comparator.naturalOrder())),
                "name", Comparator.comparing(AttributeTemplate::getName, String.CASE_INSENSITIVE_ORDER));

        List<AttributeTemplate> matchedTemplates = SearchFilter.apply(
                findAll(),
                searchParams,
                searchFields,
                sortFields);

        logger.info("Found {} matching AttributeTemplates", matchedTemplates.size());

        return matchedTemplates.stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Creates a new AttributeTemplate and refreshed the cache of valid attribute keys.
     *
     * @param dto the DTO containing the attribute template details
     * @return the created AttributeTemplate entity as AttributeTemplateResponseDTO
     * @throws DuplicateResourceException if an AttributeTemplate with the same name already exists
     */

    @Transactional
    public AttributeTemplateResponseDTO createAttributeTemplate(AttributeTemplateRequestDTO dto) {
        String normalizedName = normalize(dto.getName());

        validateAttributeTemplateName(normalizedName);

        AttributeTemplate attributeTemplate = new AttributeTemplate(normalizedName);

        AttributeTemplate createdAttributeTemplate = create(attributeTemplate);

        refreshAttributeCache();

        return mapper.toResponseDTO(createdAttributeTemplate);
    }

    /**
     * Deletes an AttributeTemplate and associated product attributes by ID.
     *
     * @param id the ID of the attribute template to delete
     * @throws ResourceNotFoundException if the attribute template does not exist
     */

    @Transactional
    public void deleteAttributeTemplate(Long id) {
        findById(id);
        deleteProductAttributesByTemplateId(id);
        delete(id);
    }

    /**
     * Deletes all product attributes associated with the given attribute template ID.
     *
     * @param id the ID of the attribute template whose product attributes are to be deleted
     */

    private void deleteProductAttributesByTemplateId(Long id) {
        productAttributeService.getProductAttributesByTemplateId(id)
                .forEach(productAttribute ->
                        productAttributeService.deleteProductAttribute(productAttribute.getId()));
    }


    /**
     * Updates an existing AttributeTemplate.
     *
     * @param id  the ID of the attribute template to update
     * @param dto the DTO containing the updated attribute template name
     * @return the updated AttributeTemplate entity as AttributeTemplateResponseDTO
     * @throws ResourceNotFoundException  if the attribute template does not exist
     * @throws DuplicateResourceException if another AttributeTemplate already exists with the same name
     */

    @Transactional
    public AttributeTemplateResponseDTO updateAttributeTemplate(Long id, AttributeTemplateUpdateDTO dto) {

        AttributeTemplate attributeTemplate = findById(id);

        logger.info("Updating AttributeTemplate with ID {}: {}. Trying to change name from '{}' to {}.", id, attributeTemplate.getName(), attributeTemplate.getName(), dto.getName() == null ? null : dto.getName());

        boolean updated = updateAttributeTemplateNameField(attributeTemplate, dto.getName());

        AttributeTemplate updatedAttributeTemplate = updated ? update(id, attributeTemplate) : attributeTemplate;

        refreshAttributeCache();

        return mapper.toResponseDTO(updatedAttributeTemplate);
    }

    /**
     * Updates the name of the given AttributeTemplate if the new name is different from the current name.
     *
     * @param attributeTemplate the AttributeTemplate to update
     * @param newName           the new name to set
     * @return true if the name was updated, false otherwise
     * @throws DuplicateResourceException if an AttributeTemplate with the new name already exists
     */

    private boolean updateAttributeTemplateNameField(AttributeTemplate attributeTemplate, String newName) {
        String normalizedName = normalize(newName);
        if (shouldUpdate(normalizedName, attributeTemplate.getName())) {
            validateAttributeTemplateName(normalizedName);
            attributeTemplate.setName(normalizedName);
            return true;
        }
        return false;
    }

    /**
     * Validates the uniqueness of the attribute template name.
     *
     * @param name the name of the attribute template to validate
     * @throws DuplicateResourceException if an attribute template with the same name already exists
     */

    private void validateAttributeTemplateName(String name) {
        if (attributeTemplateRepository.existsByName(name)) {
            throw new DuplicateResourceException(
                    String.format("Attribute template with name '%s' already exists.", name));
        }
    }

    /**
     * Retrieves a set of valid attribute keys.
     * The attribute keys are derived from the names of all attribute templates.
     * The result is cached to improve performance.
     *
     * @return a set of valid attribute keys in lowercase.
     */

    @Transactional(readOnly = true)
    @Cacheable("validAttributeKeys")
    public Set<String> getValidAttributeKeys() {
        return findAll().stream()
                .map(template -> template.getName().toLowerCase())
                .collect(Collectors.toSet());
    }

    /**
     * Evicts all entries from the cache named "validAttributeKeys".
     * This method is used to refresh the cache when attribute templates are created, updated, or deleted.
     */

    @CacheEvict(value = "validAttributeKeys", allEntries = true)
    public void refreshAttributeCache() {
    }
}

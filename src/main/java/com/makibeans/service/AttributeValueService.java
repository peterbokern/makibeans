package com.makibeans.service;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.dto.AttributeValueUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.AttributeValueMapper;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.util.MappingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AttributeValueService extends AbstractCrudService<AttributeValue, Long> {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeTemplateService attributeTemplateService;
    private final AttributeValueMapper mapper;


    @Autowired
    public AttributeValueService(AttributeValueRepository attributeValueRepository,
                                 AttributeTemplateService attributeTemplateService,
                                 AttributeValueMapper mapper) {
        super(attributeValueRepository);
        this.attributeValueRepository = attributeValueRepository;
        this.attributeTemplateService = attributeTemplateService;
        this.mapper = mapper;
    }

    /**
     * Retrieves an AttributeValue by its unique identifier.
     *
     * @param id the unique identifier of the AttributeValue to retrieve.
     * @return the AttributeValueResponseDTO representing the found attribute template.
     * @throws IllegalArgumentException if the provided id is null
     * @throws ResourceNotFoundException if no AttributeValue is found with the given id.
     */

    @Transactional(readOnly = true)
    public AttributeValueResponseDTO getAttributeValueById(Long id) {
        AttributeValue attributeValue = findById(id);
        return mapper.toResponseDTO(attributeValue);
    }

    /**
     * Retrieves all AttributeValues.
     *
     * @return the list of all AttributeValueResponseDTO's representing the found attribute values.
     */

    @Transactional(readOnly = true)
    public List<AttributeValueResponseDTO> getAllAttributeValues() {
        return findAll().stream().map(mapper::toResponseDTO).toList();
    }

    /**
     * Retrieves all AttributeValues by given AttributeTemplate id
     *
     * @param templateId the attributeTemplate id to search on
     * @return the list of all AttributeValueResponseDTO's representing the found attribute values.
     */

    @Transactional(readOnly = true)
    public List<AttributeValueResponseDTO> getAllAttributeValuesByTemplateId(Long templateId) {

        AttributeTemplate  attributeTemplate = attributeTemplateService.findById(templateId);

        return attributeValueRepository.findAllByAttributeTemplate(attributeTemplate)
                .stream()
                .map(mapper::toResponseDTO)
                .toList();
    }

    /**
     * Creates a new AttributeValue.
     *
     * @param requestDTO the DTO containing the attribute value details
     * @return the AttributeValueResponseDTO that represents the created AttributeValue entity
     * @throws DuplicateResourceException if an AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValueResponseDTO createAttributeValue(AttributeValueRequestDTO requestDTO) {

        AttributeTemplate attributeTemplate = attributeTemplateService.findById(requestDTO.getTemplateId());
        AttributeValue attributeValue = mapper.toEntity(requestDTO);
        attributeValue.setAttributeTemplate(attributeTemplate);
        String value = attributeValue.getValue();

        if (attributeValueRepository.existsByValue(attributeTemplate, value)) {
            throw new DuplicateResourceException("Attribute value '" + value + "' already exists for attribute " + attributeTemplate.getName() + ".");
        }

        AttributeValue savedAttributeValue = create(attributeValue);

        return mapper.toResponseDTO(savedAttributeValue);
    }

    /**
     * Deletes an AttributeValue by ID.
     *
     * @param id the ID of the attribute value to delete
     * @throws ResourceNotFoundException if the attribute value does not exist
     */

    @Transactional
    public void deleteAttributeValue(Long id) {
        delete(id);
    }

    /**
     * Updates an existing AttributeValue.
     *
     * @param id  the ID of the attribute value to update
     * @param dto the DTO containing the updated attribute value details
     * @return the updated AttributeValue entity
     * @throws ResourceNotFoundException  if the attribute value does not exist
     * @throws DuplicateResourceException if another AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValueResponseDTO updateAttributeValue(Long id, AttributeValueUpdateDTO dto) {

        AttributeValue attributeValue = findById(id);
        AttributeTemplate attributeTemplate = attributeValue.getAttributeTemplate();
        String normalizedValue = MappingUtils.normalizeValue(dto.getValue());

        if (!attributeValue.getValue().equalsIgnoreCase(normalizedValue) && attributeValueRepository.existsByValue(attributeTemplate, normalizedValue)) {
            throw new DuplicateResourceException("Attribute value '" + normalizedValue + "' already exists for attribute template " + attributeTemplate + ".");
        }

        attributeValue.setValue(normalizedValue);

        AttributeValue updatedAttributeValue = update(id, attributeValue);

        return mapper.toResponseDTO(updatedAttributeValue);
    }
}

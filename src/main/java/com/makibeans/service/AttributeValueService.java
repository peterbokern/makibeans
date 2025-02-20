package com.makibeans.service;

import com.makibeans.dto.AttributeValueRequestDTO;
import com.makibeans.dto.AttributeValueResponseDTO;
import com.makibeans.dto.AttributeValueUpdateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.mapper.AttributeValueMapper;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import com.makibeans.util.StringUtil;
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

    public AttributeValueResponseDTO getAttributeValueById(Long id) {
        AttributeValue attributeValue = findById(id);
        return mapper.toResponseDTO(attributeValue);
    }

    /**
     * Retrieves all AttributeValues.
     *
     * @return the list of all AttributeValueResponseDTO's representing the found attribute values.
     */

    public List<AttributeValueResponseDTO> getAllAttributeValues() {
        return findAll().stream().map(mapper::toResponseDTO).toList();
    }

    /**
     * Creates a new AttributeValue.
     *
     * @param dto the DTO containing the attribute value details
     * @return the AttributeValueResponseDTO that represents the created AttributeValue entity
     * @throws DuplicateResourceException if an AttributeValue with the same value already exists
     */

    @Transactional
    public AttributeValueResponseDTO createAttributeValue(AttributeValueRequestDTO dto) {

        String normalizedValue = StringUtil.normalize(dto.getValue());

        AttributeTemplate attributeTemplate = attributeTemplateService.findById(dto.getTemplateId());

        if (attributeValueRepository.existsByValue(attributeTemplate, normalizedValue)) {
            throw new DuplicateResourceException("Attribute value '" + normalizedValue + "' already exists.");
        }

        AttributeValue attributeValue = new AttributeValue(attributeTemplate, normalizedValue);

        return mapper.toResponseDTO(create(attributeValue));
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

        String normalizedValue = dto.getValue().trim().toLowerCase();

        if (!attributeValue.getValue().equalsIgnoreCase(normalizedValue) && attributeValueRepository.existsByValue(attributeTemplate, normalizedValue)) {
            throw new DuplicateResourceException("Attribute value '" + normalizedValue + "' already exists for attribute template " + attributeTemplate+ ".");
        }

        attributeValue.setValue(normalizedValue);

        return mapper.toResponseDTO(update(id, attributeValue));
    }
}

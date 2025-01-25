package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.model.AttributeValue;
import com.makibeans.repository.AttributeValueRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AttributeValueService extends AbstractCrudService<AttributeValue, Long> {

    private final AttributeValueRepository attributeValueRepository;
    private final AttributeTemplateService attributeTemplateService;

    @Autowired
    public AttributeValueService(JpaRepository<AttributeValue, Long> repository, AttributeValueRepository attributeValueRepository, AttributeTemplateService attributeTemplateService) {
        super(repository);
        this.attributeValueRepository = attributeValueRepository;
        this.attributeTemplateService = attributeTemplateService;
    }


    @Transactional
    public AttributeValue createAttributeValue(Long id, String value) {

        if (id == null) {
            throw new IllegalArgumentException("ID cannot be null");
        }

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }

        //Throws resourceNotFound
        AttributeTemplate attributeTemplate = attributeTemplateService.findById(id);

        if (attributeValueRepository.existsByValue(attributeTemplate, value.trim())) {
            throw new DuplicateResourceException("Attribute Value " + value + " already exists.");
        }

        AttributeValue attributeValue = new AttributeValue(attributeTemplate, value.trim());
        return create(attributeValue);
    }

    @Transactional
    public void deleteAttributeValue(Long id) {
        delete(id);
    }

    @Transactional
    public AttributeValue updateAttributeValue(Long id, String value) {

        if (id == null) {throw new IllegalArgumentException("ID cannot be null.");}

        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("Value cannot be null or empty");
        }

        AttributeValue attributeValue = findById(id);

        if (Objects.equals(attributeValue.getValue(), value)) {
            return attributeValue;
        }

        attributeValue.setValue(value);

        return update(id, attributeValue);
    }
}

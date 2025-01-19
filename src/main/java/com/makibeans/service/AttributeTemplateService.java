package com.makibeans.service;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.AttributeTemplate;
import com.makibeans.repository.AttributeTemplateRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

//public abstract class GenericService<T, ID> implements GenericService<T, ID>
@Service
public class AttributeTemplateService extends AbstractCrudService<AttributeTemplate, Long> {

    private final AttributeTemplateRepository attributeTemplateRepository;

    //public AttributeTemplateService(JpaRepository<T, Long> repository) - Constructor
        //this.repository = repository;
    @Autowired
    public AttributeTemplateService(AttributeTemplateRepository attributeTemplateRepository) {
        super(attributeTemplateRepository);
        this.attributeTemplateRepository = attributeTemplateRepository;
    }
    /**
     * Create a new attribute template
     *
     * @param name the name of the attribute template
     * @return the created attribute template
     * @throws IllegalArgumentException   if the name is null or empty
     * @throws DuplicateResourceException if the attribute template with the same name already exists
     */

    // T create(T entity) - Create a new entity
    //public T create(T entity) - Create a new entity
    @Transactional
    public AttributeTemplate createAttributeTemplate(String name) {

        //validation
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Attribute template name cannot be null or empty");
        }

        // trim input
        final String trimmedName = name.trim();

        //save attribute template if not exists
        if (attributeTemplateRepository.existsByName(trimmedName)) {
            throw new DuplicateResourceException("Attribute template with name " + trimmedName + " already exists");
        }

        return create(new AttributeTemplate(trimmedName));
    }

    /**
     * Delete an attribute template
     *
     * @param id the id of the attribute template to delete
     * @throws ResourceNotFoundException if the attribute template with the id does not exist
     */

    //void deleteById(ID id) - Delete an entity by its id
    //public void delete(ID id)

    @Transactional
    public void deleteAttributeTemplate(Long id) {delete(id);}

    /**
     * Update an attribute template
     *
     * @param id      the id of the attribute template to update
     * @param newName the new name of the attribute template
     * @return the updated attribute template
     * @throws IllegalArgumentException  if the new name is null or empty
     * @throws ResourceNotFoundException if the attribute template with the id does not exist
     */

    // T update(ID id, T entity)
    //public T update(ID id, T entity)

    @Transactional
    public AttributeTemplate updateAttributeTemplate(Long id, String newName) {

        //input validation

        if (newName == null || newName.isBlank()) {
            throw new IllegalArgumentException("New attribute template name cannot be null or empty");
        }

        // trim input
        final String trimmedNewName = newName.trim();

        //find attribute template
        AttributeTemplate attributeTemplate = findById(id);

        if (attributeTemplate.getName().equals(trimmedNewName)) {
            return attributeTemplate;
        }

        final String oldName = attributeTemplate.getName();

        //update attribute template and save
        attributeTemplate.setName(trimmedNewName);

        return update(id, attributeTemplate);
    }
}

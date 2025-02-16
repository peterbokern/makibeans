package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.model.Size;
import com.makibeans.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class SizeService extends AbstractCrudService<Size, Long> {

    private final SizeRepository sizeRepository;

    @Autowired
    public SizeService(JpaRepository<Size, Long> repository, SizeRepository sizeRepository) {
        super(repository);
        this.sizeRepository = sizeRepository;
    }

    /**
     * Creates a new Size entity.
     *
     * @param name The name of the size.
     * @return The saved Size entity.
     * @throws DuplicateResourceException If a size with the same name already exists.
     */

    @Transactional
    public Size createSize(String name) {

        if (sizeRepository.existsByName(name)) {
            throw new DuplicateResourceException("Size with name " + name + " already exists.");
        }

        Size size = new Size(name);

        return create(size);
    }

    /**
     * Deletes a Size by ID.
     *
     * @param sizeId The ID of the size to delete.
     */

    @Transactional
    public void deleteSize(Long sizeId){
        delete(sizeId);
    }

    /**
     * Updates an existing Size.
     *
     * @param sizeId      The ID of the size to update.
     * @param updatedName The new name for the size.
     * @return The updated Size entity.
     * @throws DuplicateResourceException If a size with the same name already exists.
     */

    @Transactional
    public Size updateSize(Long sizeId, String updatedName) {
        Size size = findById(sizeId);

        if (!size.getName().equals(updatedName) && sizeRepository.existsByName(updatedName)) {
            throw new DuplicateResourceException("Size with name '" + updatedName + "' already exists.");
        }

        size.setName(updatedName);

        return update(sizeId, size);
    }
}

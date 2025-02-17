package com.makibeans.service;

import com.makibeans.dto.SizeCreateDTO;
import com.makibeans.dto.SizeUpdateDTO;
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
     * @param sizeCreateDTO The DTO to create Size
     * @return The saved Size entity.
     * @throws DuplicateResourceException If a size with the same name already exists.
     */

    @Transactional
    public Size createSize(SizeCreateDTO sizeCreateDTO) {
        String normalizedName = sizeCreateDTO.getName().trim().toLowerCase();

        if (sizeRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Size with name " + sizeCreateDTO.getName() + " already exists.");
        }

        Size size = new Size(normalizedName);

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
     * @param sizeUpdateDTO The dto to update Size.
     * @return The updated Size entity.
     * @throws DuplicateResourceException If a size with the same name already exists.
     */

    @Transactional
    public Size updateSize(Long sizeId, SizeUpdateDTO sizeUpdateDTO) {
        Size size = findById(sizeId);
        String normalizedName = sizeUpdateDTO.getName().trim().toLowerCase();

        if (!size.getName().equals(normalizedName) && sizeRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("A size with the name '" + sizeUpdateDTO.getName() + "' already exists.");
        }

        size.setName(normalizedName);
        return update(sizeId, size);
    }

}

package com.makibeans.service;

import com.makibeans.dto.SizeRequestDTO;
import com.makibeans.dto.SizeResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.SizeMapper;
import com.makibeans.model.Size;
import com.makibeans.repository.ProductVariantRepository;
import com.makibeans.repository.SizeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class SizeService extends AbstractCrudService<Size, Long> {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;
    private final ProductVariantRepository productVariantRepository;

    @Autowired
    public SizeService(JpaRepository<Size, Long> repository, SizeRepository sizeRepository, SizeMapper sizeMapper, ProductVariantRepository productVariantRepository) {
        super(repository);
        this.sizeRepository = sizeRepository;
        this.sizeMapper = sizeMapper;
        this.productVariantRepository = productVariantRepository;
    }

    /**
     * Retrieves a size by its ID.
     *
     * @param id the ID of the size to retrieve.
     * @return the SizeResponseDTO representing the size.
     * @throws ResourceNotFoundException if the size does not exist.
     */

    @Transactional
    public SizeResponseDTO getSizeById(Long id) {
        Size size = findById(id);
        return sizeMapper.toResponseDTO(size);
    }

    /**
     * Retrieves all sizes.
     *
     * @return a list of SizeResponseDTO representing all sizes.
     */

    @Transactional(readOnly = true)
    public List<SizeResponseDTO> getAllSizes() {
        return sizeRepository.findAll()
                .stream()
                .map(sizeMapper::toResponseDTO)
                .toList(); // ✅ No need for collect(Collectors.toList())
    }

    /**
     * Creates a new Size entity.
     *
     * @param sizeRequestDTO The DTO to create Size
     * @return The saved SizeResponseDTO.
     * @throws DuplicateResourceException If a size with the same name already exists.
     */

    @Transactional
    public SizeResponseDTO createSize(SizeRequestDTO sizeRequestDTO) {
        String normalizedName = sizeRequestDTO.getName().trim().toLowerCase();

        if (sizeRepository.existsByName(normalizedName)) {
            throw new DuplicateResourceException("Size with name " + sizeRequestDTO.getName() + " already exists.");
        }

        Size size = new Size(normalizedName);
        Size savedSize = create(size);
        return sizeMapper.toResponseDTO(savedSize);
    }

    /**
     * Deletes a Size by ID and deletes all associated productVariants .
     *
     * @param sizeId The ID of the size to delete.
     */

    @Transactional
    public void deleteSize(Long sizeId){
        productVariantRepository.deleteBySizeId(sizeId);
        delete(sizeId);
    }

    /**
     * Updates an existing Size.
     *
     * @param sizeId        The ID of the size to update.
     * @param sizeRequestDTO The dto to update Size.
     * @return The updated SizeResponseDTO.
     * @throws DuplicateResourceException If a size with the same name already exists.
     */

    @Transactional
    public SizeResponseDTO updateSize(Long sizeId, SizeRequestDTO sizeRequestDTO) {
        Size size = findById(sizeId);
        String name = sizeRequestDTO.getName();

        if (!size.getName().equals(name) && sizeRepository.existsByName(name)) {
            throw new DuplicateResourceException("A size with the name '" + name + "' already exists.");
        }

        size.setName(name);
        Size updatedSize = update(sizeId, size);
        return sizeMapper.toResponseDTO(updatedSize);
    }
}

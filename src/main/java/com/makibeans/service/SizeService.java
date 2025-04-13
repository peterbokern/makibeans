    package com.makibeans.service;

    import com.makibeans.dto.size.SizeRequestDTO;
    import com.makibeans.dto.size.SizeResponseDTO;
    import com.makibeans.dto.size.SizeUpdateDTO;
    import com.makibeans.exceptions.DuplicateResourceException;
    import com.makibeans.exceptions.ResourceNotFoundException;
    import com.makibeans.filter.SearchFilter;
    import com.makibeans.mapper.SizeMapper;
    import com.makibeans.model.Size;
    import com.makibeans.repository.SizeRepository;
    import org.slf4j.Logger;
    import org.slf4j.LoggerFactory;
    import org.springframework.beans.factory.annotation.Autowired;
    import org.springframework.context.annotation.Lazy;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.stereotype.Service;
    import org.springframework.transaction.annotation.Transactional;

    import java.util.Comparator;
    import java.util.List;
    import java.util.Map;
    import java.util.function.Function;

    import static com.makibeans.util.UpdateUtils.normalize;
    import static com.makibeans.util.UpdateUtils.shouldUpdate;

    /**
     * Service class for managing Sizes.
     * Provides methods to retrieve, create, update, and delete sizes.
     */

    @Service
    public class SizeService extends AbstractCrudService<Size, Long> {

        private final SizeRepository sizeRepository;
        private final SizeMapper sizeMapper;
        private final Logger logger = LoggerFactory.getLogger(SizeService.class);
        private final ProductVariantService productVariantService;

        @Autowired
        public SizeService(JpaRepository<Size, Long> repository,
                           SizeRepository sizeRepository,
                           SizeMapper sizeMapper,
                           @Lazy ProductVariantService productVariantService) {
            super(repository);
            this.sizeRepository = sizeRepository;
            this.sizeMapper = sizeMapper;
            this.productVariantService = productVariantService;
        }

        /**
         * Retrieves a size by its ID.
         *
         * @param id the ID of the size to retrieve.
         * @return the SizeResponseDTO representing the size.
         * @throws ResourceNotFoundException if the size does not exist.
         */

        @Transactional(readOnly = true)
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
                    .toList();
        }

        /**
         * Finds sizes based on search query parameters.
         *
         * @param searchParams the search parameters to filter sizes
         * @return a list of SizeResponseDTO representing the matching sizes
         */

        @Transactional(readOnly = true)
        public List<SizeResponseDTO> findBySearchQuery(Map<String, String> searchParams) {

            Map<String, Function<Size, String>> searchFields = Map.of(
                    "name", Size::getName);

            Map<String, Comparator<Size>> sortFields = Map.of(
                    "id", Comparator.comparing(Size::getId, Comparator.nullsLast(Comparator.naturalOrder())),
                    "name", Comparator.comparing(Size::getName, String.CASE_INSENSITIVE_ORDER));

            List<Size> matchSizes = SearchFilter.apply(
                    findAll(),
                    searchParams,
                    searchFields,
                    sortFields);

            // Convert to response DTOs
            return matchSizes.stream()
                    .map(sizeMapper::toResponseDTO)
                    .toList();
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
            String normalizedName = normalize(sizeRequestDTO.getName());

            validateSizeName(normalizedName);

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
            productVariantService.deleteProductVariantBySizeId(sizeId);
            delete(sizeId);
        }

        /**
         * Updates an existing Size if changes are detected.
         *
         * @param sizeId        The ID of the size to update.
         * @param sizeRequestDTO The dto to update Size.
         * @return The updated SizeResponseDTO.
         * @throws DuplicateResourceException If a size with the same name already exists.
         */

        @Transactional
        public SizeResponseDTO updateSize(Long sizeId, SizeUpdateDTO sizeRequestDTO) {
            Size size = findById(sizeId);

            boolean updated = updateSizeNameField(size, sizeRequestDTO.getName());

            Size updatedSize = updated ? update(sizeId, size) : size;

            return sizeMapper.toResponseDTO(updatedSize);
        }

        /**
         * Updates the name of the given Size if the new name is different from the current name.
         *
         * @param size the Size to update
         * @param newName the new name to set
         * @return true if the name was updated, false otherwise
         * @throws DuplicateResourceException if a Size with the new name already exists
         */

        private boolean updateSizeNameField(Size size, String newName) {
          String normalizedName = normalize(newName);
            if (shouldUpdate(newName, size.getName())) {
                validateSizeName(normalizedName);
                size.setName(normalizedName);
                return true;
            }
            return false;
        }

        /**
         * Validates the uniqueness of the size name.
         *
         * @param name the name of the size to validate
         * @throws DuplicateResourceException if a size with the same name already exists
         */

        private void validateSizeName(String name) {
            if (sizeRepository.existsByName(name)) {
                throw new DuplicateResourceException(
                        String.format("Size with name '%s' already exists", name));
            }
        }
    }

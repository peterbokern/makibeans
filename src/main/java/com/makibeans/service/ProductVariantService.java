package com.makibeans.service;

import com.makibeans.dto.productvariant.ProductVariantRequestDTO;
import com.makibeans.dto.productvariant.ProductVariantResponseDTO;
import com.makibeans.dto.productvariant.ProductVariantUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductVariantMapper;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import com.makibeans.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static com.makibeans.util.UpdateUtils.shouldUpdate;

/**
 * Service class for managing Product Variants.
 * Provides methods to retrieve, create, update, and delete Product Variants.
 */

@Service
public class ProductVariantService extends AbstractCrudService<ProductVariant, Long> {

    private final ProductVariantRepository productVariantRepository;
    private final ProductService productService;
    private final SizeService sizeService;
    private final ProductVariantMapper productVariantMapper;

    @Autowired
    public ProductVariantService(
            JpaRepository<ProductVariant, Long> repository,
            ProductVariantRepository productVariantRepository,
            ProductService productService,
            SizeService sizeService,
            ProductVariantMapper productVariantMapper) {
        super(repository);
        this.productVariantRepository = productVariantRepository;
        this.productService = productService;
        this.sizeService = sizeService;
        this.productVariantMapper = productVariantMapper;
    }

    /**
     * Retrieves a Product Variant by its ID.
     *
     * @param id the ID of the Product Variant to retrieve
     * @return the ProductVariantResponseDTO representing the Product Variant
     * @throws ResourceNotFoundException if the Product Variant does not exist
     */

    @Transactional(readOnly = true)
    public ProductVariantResponseDTO getProductVariantById(Long id) {
        ProductVariant productVariant = findById(id);
        return productVariantMapper.toResponseDTO(productVariant);
    }

    /**
     * Retrieves all Product Variants.
     *
     * @return a list of ProductVariantResponseDTO representing all Product Variants
     */

    @Transactional(readOnly = true)
    public List<ProductVariantResponseDTO> getAllProductVariants() {
        return productVariantRepository.findAll()
                .stream()
                .map(productVariantMapper::toResponseDTO)
                .toList();
    }

    /**
     * Creates a new Product Variant.
     *
     * @param dto the DTO containing details for creating a Product Variant
     * @return the saved ProductVariantResponseDTO
     * @throws DuplicateResourceException if a Product Variant with the same product and size already exists
     */

    @Transactional
    public ProductVariantResponseDTO createProductVariant(ProductVariantRequestDTO dto) {
        Product product = productService.findById(dto.getProductId());
        Size size = sizeService.findById(dto.getSizeId());

        validateUniqueProductVariant(product, size);

        ProductVariant productVariant = new ProductVariant(
                product,
                size,
                dto.getPriceInCents(),
                generateSKU(product, size),
                dto.getStock()
        );

        ProductVariant savedVariant = create(productVariant);

        return productVariantMapper.toResponseDTO(savedVariant);
    }

    /**
     * Deletes a Product Variant by ID.
     *
     * @param productVariantId the ID of the Product Variant to delete
     */

    @Transactional
    public void deleteProductVariant(Long productVariantId) {
        delete(productVariantId);
    }

    /**
     * Deletes all Product Variants associated with a given Size ID.
     *
     * @param sizeId the ID of the Size whose associated Product Variants are to be deleted
     */

    @Transactional
    public void deleteProductVariantBySizeId(Long sizeId) {
        productVariantRepository.deleteBySizeId(sizeId);
    }

    /**
     * Updates an existing Product Variant.
     *
     * @param productVariantId the ID of the Product Variant to update
     * @param dto the DTO containing updated price and stock
     * @return the updated ProductVariantResponseDTO
     * @throws ResourceNotFoundException if the Product Variant does not exist
     */

    @Transactional
    public ProductVariantResponseDTO updateProductVariant(Long productVariantId, ProductVariantUpdateDTO dto) {
        ProductVariant productVariant = findById(productVariantId);

        boolean updated = false;

        updated |= updatePriceInCentsField(productVariant, dto.getPriceInCents());
        updated |= updateStockField(productVariant, dto.getStock());
        updated |= updateSkuField(productVariant);

        ProductVariant updatedVariant = updated ? update(productVariantId, productVariant) : productVariant;
        return productVariantMapper.toResponseDTO(updatedVariant);
    }

    /**
     * Generates a unique SKU based on product and size.
     *
     * @param product the associated Product entity
     * @param size the associated Size entity
     * @return the generated SKU
     */

    private String generateSKU(Product product, Size size) {
        String productCode = product.getProductName().replaceAll("\\s+", "").toUpperCase();
        String sizeCode = size.getName().replaceAll("\\s+", "").toUpperCase();
        String uniqueNumber = String.format("%04d", ThreadLocalRandom.current().nextInt(10000));

        return productCode + "-" + sizeCode + "-" + uniqueNumber;
    }

    /**
     * Updates the price in cents field of the Product Variant if it has changed.
     *
     * @param productVariant the Product Variant to update
     * @param newPriceInCents the new price in cents
     * @return true if the price was updated, false otherwise
     */

    private boolean updatePriceInCentsField(ProductVariant productVariant, Long newPriceInCents) {
        if (shouldUpdate(newPriceInCents, productVariant.getPriceInCents())) {
            productVariant.setPriceInCents(newPriceInCents);
            return true;
        }
        return false;
    }

    /**
     * Updates the stock field of the Product Variant if it has changed.
     *
     * @param productVariant the Product Variant to update
     * @param newStock the new stock value
     * @return true if the stock was updated, false otherwise
     */

    private boolean updateStockField(ProductVariant productVariant, Long newStock) {
        if (shouldUpdate(newStock, productVariant.getStock())) {
            productVariant.setStock(newStock);
            return true;
        }
        return false;
    }

    /**
     * Updates the SKU field of the Product Variant if it has changed.
     *
     * @param productVariant the Product Variant to update
     * @return true if the SKU was updated, false otherwise
     */

    private boolean updateSkuField(ProductVariant productVariant) {
        String newSku = generateSKU(productVariant.getProduct(), productVariant.getSize());
        if (shouldUpdate(newSku, productVariant.getSku())) {
            productVariant.setSku(newSku);
            return true;
        }
        return false;
    }

    /**
     * Validates the uniqueness of a Product Variant based on the given product and size.
     * Throws a DuplicateResourceException if a Product Variant with the same product and size already exists.
     *
     * @param product the Product entity to check
     * @param size the Size entity to check
     * @throws DuplicateResourceException if a Product Variant with the same product and size already exists
     */

    private void validateUniqueProductVariant(Product product, Size size) {
        if (productVariantRepository.existsByProductAndSize(product, size)) {
            throw new DuplicateResourceException(
                    "A ProductVariant with product ID " + product.getId() + " and size ID " + size.getId() + " already exists."
            );
        }
    }
}

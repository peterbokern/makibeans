package com.makibeans.service;

import com.makibeans.dto.ProductVariantCreateDTO;
import com.makibeans.dto.ProductVariantUpdateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import com.makibeans.repository.ProductVariantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

@Service
public class ProductVariantService extends AbstractCrudService<ProductVariant, Long> {

    private final ProductVariantRepository productVariantRepository;
    private final ProductService productService;
    private final SizeService sizeService;

    @Autowired
    public ProductVariantService(
            JpaRepository<ProductVariant, Long> repository,
            ProductVariantRepository productVariantRepository,
            ProductService productService,
            SizeService sizeService) {
        super(repository);
        this.productVariantRepository = productVariantRepository;
        this.productService = productService;
        this.sizeService = sizeService;
    }

    /**
     * Creates a new ProductVariant.
     *
     * @param dto The DTO containing details for creating a ProductVariant.
     * @return The saved ProductVariant entity.
     * @throws DuplicateResourceException If a ProductVariant with the same product and size already exists.
     */

    @Transactional
    public ProductVariant createProductVariant(ProductVariantCreateDTO dto) {
        Product product = productService.findById(dto.getProductId());
        Size size = sizeService.findById(dto.getSizeId());

        if (productVariantRepository.existsByProductAndSize(product, size)) {
            throw new DuplicateResourceException(
                    "A ProductVariant with product ID " + product.getId() + " and size ID " + size.getId() + " already exists."
            );
        }

        ProductVariant productVariant = new ProductVariant(
                product,
                size,
                dto.getPriceInCents(),
                generateSKU(product, size),
                dto.getStock()
        );

        return create(productVariant);
    }

    /**
     * Deletes a ProductVariant by ID.
     *
     * @param productVariantId The ID of the ProductVariant to delete.
     * @throws ResourceNotFoundException If the ProductVariant does not exist.
     */

    @Transactional
    public void deleteProductVariant(Long productVariantId) {
        delete(productVariantId);
    }

    /**
     * Updates an existing ProductVariant.
     *
     * @param productVariantId The ID of the ProductVariant to update.
     * @param dto The DTO containing updated price and stock.
     * @return The updated ProductVariant entity.
     * @throws ResourceNotFoundException If the ProductVariant does not exist.
     */

    @Transactional
    public ProductVariant updateProductVariant(Long productVariantId, ProductVariantUpdateDTO dto) {
        ProductVariant productVariant = productVariantRepository.findById(productVariantId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "ProductVariant with ID " + productVariantId + " does not exist."
                ));

        productVariant.setPriceInCents(dto.getPriceInCents());
        productVariant.setStock(dto.getStock());

        return update(productVariantId, productVariant);
    }

    /**
     * Generates a unique SKU based on product and size.
     *
     * @param product The associated Product entity.
     * @param size The associated Size entity.
     * @return The generated SKU.
     */
    private String generateSKU(Product product, Size size) {
        String productCode = product.getProductName().replaceAll("\\s+", "").toUpperCase();
        String sizeCode = size.getName().replaceAll("\\s+", "").toUpperCase();
        String uniqueNumber = String.format("%04d", new Random().nextInt(10000));

        return productCode + "-" + sizeCode + "-" + uniqueNumber;
    }
}

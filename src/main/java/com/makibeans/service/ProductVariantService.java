package com.makibeans.service;

import com.makibeans.dto.ProductVariantRequestDTO;
import com.makibeans.dto.ProductVariantResponseDTO;
import com.makibeans.dto.ProductVariantUpdateDTO;
import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.exeptions.ResourceNotFoundException;
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
import java.util.Random;

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

    @Transactional
    public ProductVariantResponseDTO createProductVariant(ProductVariantRequestDTO dto) {
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

        ProductVariant savedVariant = create(productVariant);
        return productVariantMapper.toResponseDTO(savedVariant);
    }

    @Transactional(readOnly = true)
    public ProductVariantResponseDTO getProductVariantById(Long id) {
        ProductVariant productVariant = findById(id);
        return productVariantMapper.toResponseDTO(productVariant);
    }

    @Transactional(readOnly = true)
    public List<ProductVariantResponseDTO> getAllProductVariants() {
        return productVariantRepository.findAll()
                .stream()
                .map(productVariantMapper::toResponseDTO)
                .toList();
    }

    @Transactional
    public void deleteProductVariant(Long productVariantId) {
        delete(productVariantId);
    }

    @Transactional
    public ProductVariantResponseDTO updateProductVariant(Long productVariantId, ProductVariantUpdateDTO dto) {
        ProductVariant productVariant = findById(productVariantId);
        productVariant.setPriceInCents(dto.getPriceInCents());
        productVariant.setStock(dto.getStock());

        ProductVariant updatedVariant = update(productVariantId, productVariant);
        return productVariantMapper.toResponseDTO(updatedVariant);
    }

    private String generateSKU(Product product, Size size) {
        String productCode = product.getProductName().replaceAll("\\s+", "").toUpperCase();
        String sizeCode = size.getName().replaceAll("\\s+", "").toUpperCase();
        String uniqueNumber = String.format("%04d", new Random().nextInt(10000));

        return productCode + "-" + sizeCode + "-" + uniqueNumber;
    }
}

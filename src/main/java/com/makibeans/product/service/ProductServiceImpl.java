package com.makibeans.product.service;

import com.makibeans.audit.model.DeleteReason;
import com.makibeans.product.dto.*;
import com.makibeans.product.filter.ProductAdminFilter;
import com.makibeans.product.filter.ProductPublicFilter;
import com.makibeans.product.repository.PriceRange;
import com.makibeans.productattribute.service.ProductAttributeService;
import com.makibeans.productattributevalue.service.ProductAttributeValueService;
import com.makibeans.productvariant.dto.ProductVariantPublicResponseDTO;
import com.makibeans.productvariant.mapper.ProductVariantMapper;
import com.makibeans.productvariant.service.ProductVariantService;
import com.makibeans.web.exceptions.*;
import com.makibeans.product.mapper.ProductMapper;
import com.makibeans.category.model.Category;
import com.makibeans.product.model.Product;
import com.makibeans.product.repository.ProductRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.category.service.CategoryService;
import com.makibeans.common.util.ImageUtils;
import com.makibeans.common.util.TextUtils;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Service class for managing Products.
 * Provides methods to retrieve, create, update, and delete Products.
 */
@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final CategoryService categoryService;
    private final ProductMapper mapper;
    private final ImageUtils imageUtils;
    private final ProductAttributeService productAttributeService;
    private final ProductAttributeValueService productAttributeValueService;
    private final ProductVariantService productVariantService;
    private final ProductSearchService productSearchService;


    public ProductServiceImpl(
            ProductRepository repo,
            CategoryService categoryService,
            ProductMapper mapper, ProductVariantMapper productVariantMapper,
            ImageUtils imageUtils,
            @Lazy ProductAttributeService productAttributeService,
            @Lazy ProductAttributeValueService productAttributeValueService,
            @Lazy ProductVariantService productVariantService,
            ProductSearchService productSearchService) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.imageUtils = imageUtils;
        this.productAttributeService = productAttributeService;
        this.productAttributeValueService = productAttributeValueService;
        this.productVariantService = productVariantService;
        this.productSearchService = productSearchService;
    }

    @Transactional(readOnly = true)
    @Override
    public Product findById(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID " + id + " not found."));
    }

    @Transactional(readOnly = true)
    @Override
    public Product findByIdIncludingDeleted(Long id) {
        return repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID " + id + " not found."));
    }

    @Transactional(readOnly = true)
    @Override
    public ProductPublicResponseDTO getById(Long id) {
        return toPublicResponseDTO(findByIdIncludingDeleted(id));
    }

    @Transactional(readOnly = true)
    @Override
    public ProductAdminResponseDTO getByIdIncludingDeleted(Long id) {
        return toAdminResponseDTO(findByIdIncludingDeleted(id));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductPublicResponseDTO> searchPublic(SearchRequest<ProductPublicFilter> req) {
        return toPublicResponseDTOPage(productSearchService.search(req, ProductPublicFilter.class));
    }

    //maps product page to dto page using mapstruct mapper with context. Mapstruct needs to have the map of price ranges to fill the price field
    @Transactional(readOnly = true)
    protected Page<ProductPublicResponseDTO> toPublicResponseDTOPage(Page<Product> products) {
        var productIds = products.stream().map(Product::getId).toList();
        var priceRanges = getPriceRangesForProducts(productIds);
        return products.map(p -> mapper.toPublicResponseDTO(p, priceRanges));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductAdminResponseDTO> searchAdmin(SearchRequest<ProductAdminFilter> req) {
        return toAdminResponseDTOPage(productSearchService.search(req, ProductAdminFilter.class));
    }

    @Transactional(readOnly = true)
    protected ProductPublicResponseDTO toPublicResponseDTO(Product product) {
        return mapper.toPublicResponseDTO(product, getPriceRangesForProducts(List.of(product.getId())));
    }

    @Transactional(readOnly = true)
    protected Page<ProductAdminResponseDTO> toAdminResponseDTOPage(Page<Product> products) {
        var productIds = products.stream().map(Product::getId).toList();
        var priceRanges = getPriceRangesForProducts(productIds);
        return products.map(p -> mapper.toAdminResponseDTO(p, priceRanges));
    }

    @Transactional(readOnly = true)
    protected ProductAdminResponseDTO toAdminResponseDTO(Product product) {
        return mapper.toAdminResponseDTO(product, getPriceRangesForProducts(List.of(product.getId())));
    }

    /**
     * Creates a new product.
     *
     * @param dto the DTO containing product details.
     * @return the saved Product.
     * @throws DuplicateResourceException if a product with the given slug already exists.
     */

    @Transactional
    @Override
    public ProductAdminResponseDTO create(ProductRequestDTO dto) {

        String name = dto.getName().trim();
        String normalizedName = normalize(name);
        String slug = TextUtils.toSlug(normalizedName);

        assertUniqueSlug(slug);

        Category category = categoryService.getById(dto.getCategoryId());

        Product product = Product.builder()
                .name(name)
                .description(dto.getDescription().trim())
                .category(category)
                .build();

        product.setSlug(slug);

        Product saved = repo.save(product);
        return toAdminResponseDTO(saved);
    }

    @Override
    @Transactional
    public ProductAdminResponseDTO update(Long productId, @Valid ProductUpdateDTO dto) {
        Product product = findById(productId);

        String newName = dto.getName();

        if (newName != null) {
            String trimmed = newName.trim();
            if (trimmed.isEmpty()) throw new IllegalArgumentException("Product name cannot be empty.");

            String normalizedNewName = TextUtils.normalizeText(newName);
            String slug = TextUtils.toSlug(normalizedNewName);

            if (slug != null && !slug.equals(product.getSlug())) {
                assertUniqueSlugAndIdNotAmongActive(slug, productId);
                product.setSlug(slug);
                product.setName(normalizedNewName);
            }
        }
        // If your mapper updates name/slug, configure it to ignore those fields.
        mapper.updateEntityFromDTO(dto, product);

        return toAdminResponseDTO(product);
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Product product = findById(productId);
        //  productAttributes and productVariants in Product entity
        if (product.isDeleted()) return;

        DeleteReason reason = DeleteReason.PRODUCT_DELETED;

        productAttributeValueService.deleteByProductId(productId, reason);
        productAttributeService.deleteByProductId(productId, reason);
        productVariantService.deleteByProductId(productId, reason);

        product.setDeleted(true);
    }

    @Override
    @Transactional
    public void restore(Long productId) {
        Product product = findByIdIncludingDeleted(productId);

        if (!product.isDeleted()) return;

        // check if unique by slug among non-deleted items
        assertUniqueSlugAndIdNotAmongActive(product.getSlug(), productId);
        product.setDeleted(false);
    }

    @Override
    @Transactional
    public ProductAdminResponseDTO uploadProductImage(Long productId, MultipartFile image) throws ImageProcessingException {
        Product product = findById(productId);

        byte[] imageBytes = imageUtils.validateAndExtractImageBytes(image);
        product.setImage(imageBytes);

        return toAdminResponseDTO(product);
    }

    /**
     * Retrieves the image of a product by its ID.
     *
     * @param productId the ID of the product whose image is to be retrieved.
     * @return a byte array representing the product image.
     */
    @Transactional(readOnly = true)
    @Override
    public byte[] getProductImage(Long productId) {
        Product product = findById(productId);
        byte[] productImage = product.getImage();

        if (productImage == null) {
            throw new ResourceNotFoundException("Product with ID " + productId + " does not have an image.");
        }

        return productImage;
    }

    /**
     * Deletes the image of a product by its ID.
     *
     * @param productId the ID of the product whose image is to be deleted.
     */
    @Transactional
    @Override
    public void deleteProductImage(Long productId) {
        Product product = findById(productId);
        product.setImage(null);
    }

    @Transactional(readOnly = true)
    @Override
    public Map<Long, PriceRange> getPriceRangesForProducts(List<Long> productIds) {
        var ranges = repo.findPriceRangesForProducts(productIds);
        if (ranges.isEmpty()) return Collections.emptyMap();
        Map<Long, PriceRange> map = new HashMap<>();
        ranges.stream()
                .filter(pr -> pr.getProductId() != null)
                .forEach(pr -> map.put(pr.getProductId(), pr));
        return map;
    }


    private void assertUniqueSlug(String slug) {
        if (slug != null && repo.existsBySlugAndDeletedFalse(slug)) {
            throw new DuplicateResourceException(
                    "Product with slug '" + slug + "' already exists.");
        }
    }

    private void assertUniqueSlugAndIdNotAmongActive(String slug, Long id) {
        if (slug != null && repo.existsBySlugAndIdNotAndDeletedFalse(slug, id)) {
            throw new DuplicateResourceException(
                    "Product with slug '" + slug + "' already exists.");
        }
    }

    private String normalize(String in) {
        return (in == null) ? null : TextUtils.normalizeText(in);
    }
}

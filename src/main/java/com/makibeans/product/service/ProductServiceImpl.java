package com.makibeans.product.service;

import com.makibeans.product.dto.ProductRequestDTO;
import com.makibeans.product.dto.ProductUpdateDTO;
import com.makibeans.product.filter.ProductAdminFilter;
import com.makibeans.product.filter.ProductFilter;
import com.makibeans.product.filter.ProductPublicFilter;
import com.makibeans.web.exceptions.DuplicateResourceException;
import com.makibeans.web.exceptions.ImageProcessingException;
import com.makibeans.web.exceptions.ResourceNotFoundException;
import com.makibeans.product.mapper.ProductMapper;
import com.makibeans.category.model.Category;
import com.makibeans.product.model.Product;
import com.makibeans.product.repository.ProductRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.category.service.CategoryService;
import com.makibeans.common.util.ImageUtils;
import com.makibeans.common.util.TextUtils;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.Objects;

/**
 * Service class for managing Products.
 * Provides methods to retrieve, create, update, and delete Products.
 */
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductRepository repo;
    private final CategoryService categoryService;
    private final ProductMapper mapper;
    private final ImageUtils imageUtils;

    @Autowired
    public ProductServiceImpl(
            ProductRepository repo,
            CategoryService categoryService,
            ProductMapper mapper,
            ImageUtils imageUtils
    ) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.imageUtils = imageUtils;
    }

    @Transactional(readOnly = true)
    @Override
    public Product getById(Long id) {
        return repo.findByIdAndDeletedFalse(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID " + id + " not found."));
    }

    @Transactional(readOnly = true)
    @Override
    public Product getByIdIncludingDeleted(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Product with ID " + id + " not found."));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchPublic(SearchRequest<ProductPublicFilter> req) {
        return search(req, ProductPublicFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Product> searchAdmin(SearchRequest<ProductAdminFilter> req) {
        return search(req, ProductAdminFilter.class);
    }

    @Override
    @Transactional(readOnly = true)
    public  <F> Page<Product> search(SearchRequest<F> req, Class<F> filterClass) {
        Specification<Product> spec =
                SpecificationFactory.fromRequest(req, filterClass);

        Sort sort = new SortResolver(filterClass)
                .resolve(req.getSortBy(), req.getSortDirection());

        Specification<Product> distinctSpec = (root, query, cb) -> {
            Objects.requireNonNull(query, "CriteriaQuery must not be null");
            query.distinct(true);
            return cb.conjunction();
        };

        Specification<Product> finalSpec = (spec == null) ? distinctSpec : spec.and(distinctSpec);

        Pageable pageable = PageRequest.of(
                req.getPage() != null ? req.getPage() : 0,
                req.getSize() != null ? req.getSize() : 20,
                sort
        );

        return repo.findAll(finalSpec, pageable);
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
    public Product create(ProductRequestDTO dto) {

        String name = dto.getName().trim();
        String normalizedName = TextUtils.normalizeText(name);
        String slug = TextUtils.toSlug(normalizedName);

        assertUniqueSlug(slug);

        Category category = categoryService.getById(dto.getCategoryId());

        Product product = Product.builder()
                .name(name)
                .description(dto.getDescription().trim())
                .category(category)
                .build();

        product.setSlug(slug);

        return repo.save(product);
    }

    @Override
    @Transactional
    public Product update(Long productId, @Valid ProductUpdateDTO dto) {
        Product product = getById(productId);

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

        return product;
    }

    @Override
    @Transactional
    public void delete(Long productId) {
        Product product = getById(productId);
        // CascadeType.ALL and orphanRemoval = true on productAttributes and productVariants in Product entity
        product.setDeleted(true);
    }

    @Override
    @Transactional
    public void restore(Long productId) {
        Product product = getByIdIncludingDeleted(productId);

        if (!Boolean.TRUE.equals(product.isDeleted())) {
            return; // or throw BadRequestException("Product is not deleted.")
        }

        // check if unique by slug among non-deleted items
        assertUniqueSlugAndIdNotAmongActive(product.getSlug(), productId);
        product.setDeleted(false);
    }

    @Override
    @Transactional
    public Product uploadProductImage(Long productId, MultipartFile image) throws ImageProcessingException {
        Product product = getById(productId);

        byte[] imageBytes = imageUtils.validateAndExtractImageBytes(image);
        product.setImage(imageBytes);

        return product;
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
        Product product = getById(productId);
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
        Product product = getById(productId);
        product.setImage(null);
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
}

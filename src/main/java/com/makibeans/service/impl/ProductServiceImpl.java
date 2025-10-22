package com.makibeans.service.impl;

import com.makibeans.dto.product.ProductRequestDTO;
import com.makibeans.dto.product.ProductResponseDTO;
import com.makibeans.dto.product.ProductUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.exceptions.ImageProcessingException;
import com.makibeans.exceptions.ResourceNotFoundException;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.ProductRepository;
import com.makibeans.search.SearchRequest;
import com.makibeans.search.SortResolver;
import com.makibeans.search.SpecificationFactory;
import com.makibeans.search.filters.ProductFilter;
import com.makibeans.service.service.CrudService;
import com.makibeans.service.service.ProductService;
import com.makibeans.util.ImageUtils;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;

import static com.makibeans.util.UpdateUtils.*;

/**
 * Service class for managing Products.
 * Provides methods to retrieve, create, update, and delete Products.
 */

@Service
public class ProductServiceImpl implements ProductService, CrudService<Product, Long> {

    private final ProductRepository repo;
    private final CategoryService categoryService;
    private final ProductMapper mapper;
    private final Logger logger = LoggerFactory.getLogger(ProductService.class);
    private final ImageUtils imageUtils;


    @Autowired
    public ProductServiceImpl(
            ProductRepository repo,
            CategoryService categoryService,
            ProductMapper mapper,
            ImageUtils imageUtils) {
        this.repo = repo;
        this.categoryService = categoryService;
        this.mapper = mapper;
        this.imageUtils = imageUtils;
    }

    /**
     * Implementors must return their repository.
     */
    @Override
    public JpaRepository<Product, Long> repo() {
        return this.repo;
    }


    @Transactional(readOnly = true)
    @Override
    public ProductResponseDTO getById(Long id) {
        Product product = getOrThrow(id);

        logger.info("product: {}", product);
        return mapper.toResponseDTO(product);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponseDTO> search(SearchRequest<ProductFilter> req) {
        Specification<Product> spec =
                SpecificationFactory.fromRequest(req, ProductFilter.class);

        Sort sort = new SortResolver(ProductFilter.class)
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

        return repo.findAll(finalSpec, pageable).map(mapper::toResponseDTO);
    }

    /**
     * Creates a new product.
     *
     * @param dto the DTO containing product details.
     * @return the saved ProductResponseDTO.
     * @throws DuplicateResourceException if a product with the given name already exists.
     */

    @Transactional
    public ProductResponseDTO create(ProductRequestDTO dto) {

        String normalizedName = normalize(dto.getName());
        assertUniqueName(normalizedName);

        Category category = categoryService.getOrThrow(dto.getCategoryId());

        Product product = Product.builder()
                .name(normalizedName)
                .description(dto.getDescription())
                .category(category)
                .build();

        Product savedProduct = repo.save(product);
        return mapper.toResponseDTO(savedProduct);
    }

    @Transactional
    public ProductResponseDTO update(Long productId, @Valid ProductUpdateDTO dto) {
        Product product = getOrThrow(productId);
        String normalizedName = normalize(dto.getName());
        assertUniqueNameAndIdNot(normalizedName, productId);
        mapper.updateEntityFromDTO(dto, product);
        return mapper.toResponseDTO(product);
    }

    @Override
    public Boolean existByCategoryId(Long categoryId) {
        return repo.existsByCategoryId(categoryId);
    }


    @Transactional
    public void delete(Long productId) {
        //deleteProductAttributes(productId); no longer needed due to CascadeType.ALL and orphanRemoval = true on productAttributes in Product entity
        softDelete(productId);
    }

    @Transactional
    public Boolean existsByCategoryId(Long categoryId) {
        return repo.existsByCategoryId(categoryId);
    }


    @Transactional
    public ProductResponseDTO uploadProductImage(Long productId, MultipartFile image) throws ImageProcessingException {
        Product product = getOrThrow(productId);

        byte[] imageBytes = imageUtils.validateAndExtractImageBytes(image);

        product.setImage(imageBytes);

        return mapper.toResponseDTO(product);
    }

    /**
     * Retrieves the image of a product by its ID.
     *
     * @param productId the ID of the product whose image is to be retrieved.
     * @return a byte array representing the product image.
     */

    @Transactional
    public byte[] getProductImage(Long productId) {
        Product product = getOrThrow(productId);
        byte[] productImage = product.getImage();
        if (productImage == null) {
            throw new ResourceNotFoundException("Product with ID " + productId + " does not have an image.");
        }
        return product.getImage();
    }

    /**
     * Deletes the image of a product by its ID.
     *
     * @param productId the ID of the product whose image is to be deleted.
     */

    @Transactional
    public void deleteProductImage(Long productId) {
        Product product = getOrThrow(productId);
        product.setImage(null);
    }


    private void assertUniqueName(String name) {
        if (name != null && repo.existsByName(name)) {
            throw new DuplicateResourceException(
                    "Product with name '" + name + "' already exists.");
        }

    }

    private void assertUniqueNameAndIdNot(String name, Long id) {
        if (name != null && repo.existsByNameAndIdNot(name, id)) {
            throw new DuplicateResourceException(
                    "Product with name '" + name + "' already exists.");
        }
    }
}



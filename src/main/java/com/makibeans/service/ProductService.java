package com.makibeans.service;

import com.makibeans.dto.ProductPageDTO;
import com.makibeans.dto.ProductRequestDTO;
import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.dto.ProductUpdateDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.ProductRepository;
import com.makibeans.filter.ProductFilter;
import jakarta.validation.Valid;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.makibeans.util.UpdateUtils.normalize;
import static com.makibeans.util.UpdateUtils.shouldUpdate;

/**
 * Service class for managing Products.
 * Provides methods to retrieve, create, update, and delete Products.
 */

@Service
public class ProductService extends AbstractCrudService<Product, Long> {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;
    private final AttributeTemplateService attributeTemplateService;


    @Autowired
    public ProductService(
            JpaRepository<Product, Long> repository,
            ProductRepository productRepository,
            CategoryService categoryService,
            ProductMapper productMapper, AttributeTemplateService attributeTemplateService) {
        super(repository);
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
        this.attributeTemplateService = attributeTemplateService;
    }

    /**
     * Retrieves a product by its ID.
     *
     * @param productId the ID of the product to retrieve.
     * @return the ProductResponseDTO representing the product.
     */

    @Transactional(readOnly = true)
    public ProductResponseDTO getProductById(Long productId) {
        Product product = findById(productId);
        return productMapper.toResponseDTO(product);
    }

    /**
     * Retrieves a list of products by the given category ID.
     *
     * @param categoryId the ID of the category to retrieve products for.
     * @return a list of products belonging to the specified category.
     */

    @Transactional
    public List<Product> getProductsByCategoryId(Long categoryId) {
        return productRepository.findProductsByCategoryId(categoryId);
    }

    /**
     * Filters products based on various criteria provided in the filters map.
     * The filters can include category ID, category name, price range, size, SKU, stock, and custom attributes.
     * Uses ProductFilter to handle the filtering and pagination logic.
     * Additionally, you can search on product name, description, attribute template, and attribute value.
     *
     * @param filters a map containing the filter criteria as key-value pairs.
     *                Supported keys: "categoryId", "categoryName", "minPrice", "maxPrice", "sizeId", "sizeName", "sku", "stock", "query", "sort", "order", "page", "size".
     *                Any other keys will be treated as custom attribute filters.
     * @return a ProductPageDTO representing the filtered products.
     */

    @Transactional
    public ProductPageDTO findBySearchQuery(Map<String, String> filters) {

        ProductFilter productFilter = ProductFilter.builder()
                .filters(filters)
                .products(findAll())
                .productMapper(productMapper)
                .validAttributeKeys(attributeTemplateService.getValidAttributeKeys())
                .build();
        return productFilter.apply();
    }

    /**
     * Creates a new product.
     *
     * @param dto the DTO containing product details.
     * @return the saved ProductResponseDTO.
     * @throws DuplicateResourceException if a product with the given name already exists.
     */

    @Transactional
    public ProductResponseDTO createProduct(ProductRequestDTO dto) {

        validateUniqueProductName(dto.getProductName());

        Category category = categoryService.findById(dto.getCategoryId());

        Product product = Product.builder()
                .productName(normalize(dto.getProductName()))
                .productDescription(normalize(dto.getProductDescription()))
                .productImageUrl((dto.getProductImageUrl() != null) ? normalize(dto.getProductImageUrl()) : null)
                .category(category)
                .build();

        Product savedProduct = productRepository.save(product);
        return productMapper.toResponseDTO(savedProduct);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId the ID of the product to delete.
     */

    @Transactional
    public void deleteProduct(Long productId) {
        delete(productId);
    }

    /**
     * Updates an existing product.
     *
     * @param productId the ID of the product to update.
     * @param dto       the DTO containing updated product details.
     * @return the updated ProductResponseDTO.
     * @throws DuplicateResourceException if a product with the given name already exists.
     */

    @Transactional
    public ProductResponseDTO updateProduct(Long productId, @Valid ProductUpdateDTO dto) {
        Product product = findById(productId);

        boolean updated = false;

        updated |= updateProductNameField(product, dto.getProductName());
        updated |= updateCategoryField(product, dto.getCategoryId());
        updated |= updateProductDescriptionField(product, dto.getProductDescription());
        updated |= updateProductImageUrlField(product, dto.getProductImageUrl());

        Product updatedProduct = updated ? update(productId, product) : product;

        return productMapper.toResponseDTO(updatedProduct);
    }

    /**
     * Validates that the new product name is unique.
     *
     * @param newProductName the new product name to check for uniqueness
     * @throws DuplicateResourceException if a product with the given name already exists
     */

    private void validateUniqueProductName(String newProductName) {
        if (productRepository.existsByProductName(newProductName)) {
            throw new DuplicateResourceException("Product with name " + newProductName + " already exists.");
        }
    }

    /**
     * Updates the product name field if it has changed.
     *
     * @param product the product to update
     * @param newProductName the new product name
     * @return true if the product name was updated, false otherwise
     * @throws DuplicateResourceException if a product with the given name already exists
     */

    private boolean updateProductNameField(Product product, String newProductName) {
        String normalizedNewProductName = normalize(newProductName);
        if (shouldUpdate(newProductName, product.getProductName())) {
            validateUniqueProductName(newProductName);
            product.setProductName(normalizedNewProductName);
           return true;
        }
        return false;
    }

    /**
     * Updates the category field of the product if it has changed.
     *
     * @param product the product to update
     * @param newCategoryId the new category ID
     * @return true if the category was updated, false otherwise
     */

    private boolean updateCategoryField(Product product, Long newCategoryId) {

        if (shouldUpdate(product.getCategory().getId(), newCategoryId)) {
            Category newCategory = categoryService.findById(newCategoryId);
            product.setCategory(newCategory);
            return true;
        }
        return false;
    }

    /**
     * Updates the product description field if it has changed.
     *
     * @param product the product to update
     * @param newProductDescription the new product description
     */

    private boolean updateProductDescriptionField (Product product, String newProductDescription) {
        String normalizedNewProductDescription = normalize(newProductDescription);
        if (shouldUpdate(newProductDescription, product.getProductDescription())) {
            product.setProductDescription(normalizedNewProductDescription);
            return true;
        }
        return false;
    }

    /**
     * Updates the product image URL field if it has changed.
     *
     * @param product the product to update
     * @param newProductImageUrl the new product image URL
     * @return true if the product image URL was updated, false otherwise
     */

    private boolean updateProductImageUrlField(Product product, String newProductImageUrl) {
        String normalizedNewProductImageUrl = newProductImageUrl != null ? newProductImageUrl.trim() : null;
        if (shouldUpdate(newProductImageUrl, product.getProductImageUrl())) {
            product.setProductImageUrl(normalizedNewProductImageUrl);
            return true;
        }
        return false;
    }
}

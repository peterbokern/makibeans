package com.makibeans.service;

import com.makibeans.dto.ProductPageDTO;
import com.makibeans.dto.ProductRequestDTO;
import com.makibeans.dto.ProductResponseDTO;
import com.makibeans.exceptions.DuplicateResourceException;
import com.makibeans.mapper.ProductMapper;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.repository.ProductRepository;
import com.makibeans.util.FilterUtils;
import com.makibeans.util.ProductFilter;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Service class for managing Products.
 * Provides methods to retrieve, create, update, and delete Products.
 */

@Service
public class ProductService extends AbstractCrudService<Product, Long> {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;
    private final ProductMapper productMapper;


    @Autowired
    public ProductService(
            JpaRepository<Product, Long> repository,
            ProductRepository productRepository,
            CategoryService categoryService,
            ProductMapper productMapper) {
        super(repository);
        this.productRepository = productRepository;
        this.categoryService = categoryService;
        this.productMapper = productMapper;
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
     * Retrieves all products.
     *
     * @return a list of ProductResponseDTO representing all products.
     */

    @Transactional(readOnly = true)
    public List<ProductResponseDTO> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponseDTO)
                .toList();
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
    public ProductPageDTO filterProducts(Map<String, String> filters) {

        ProductFilter productFilter = new ProductFilter(filters, findAll());
        return productFilter.filterAndPaginate(productMapper);
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

        if (productRepository.existsByProductName(dto.getProductName())) {
            throw new DuplicateResourceException("Product with name " + dto.getProductName() + " already exists.");
        }

        Category category = categoryService.findById(dto.getCategoryId());

        Product product = Product.builder()
                .productName(dto.getProductName().trim().toLowerCase())
                .productDescription(dto.getProductDescription().trim().toLowerCase())
                .productImageUrl((dto.getProductImageUrl() != null) ? dto.getProductImageUrl().trim() : null)
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
    public ProductResponseDTO updateProduct(Long productId, ProductRequestDTO dto) {
        Product productToUpdate = findById(productId);

        if (!productToUpdate.getProductName().equals(dto.getProductName()) &&
                productRepository.existsByProductName(dto.getProductName())) {
            throw new DuplicateResourceException("Product with name " + dto.getProductName() + " already exists.");
        }

        Category category = categoryService.findById(dto.getCategoryId());

        productToUpdate.setProductName(dto.getProductName().trim().toLowerCase());
        productToUpdate.setProductDescription(dto.getProductDescription().trim().toLowerCase());
        productToUpdate.setCategory(category);
        productToUpdate.setProductImageUrl(
                dto.getProductImageUrl() != null ? dto.getProductImageUrl().trim() : null
        );

        Product updatedProduct = productRepository.save(productToUpdate);
        return productMapper.toResponseDTO(updatedProduct);
    }
}

package com.makibeans.service;

import com.makibeans.exeptions.DuplicateResourceException;
import com.makibeans.model.Category;
import com.makibeans.model.Product;
import com.makibeans.repository.CategoryRepository;
import com.makibeans.repository.ProductRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

@Service
public class ProductService extends AbstractCrudService<Product, Long> {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Autowired
    public ProductService(JpaRepository<Product, Long> repository, ProductRepository productRepository, CategoryRepository productCategoryRepository, CategoryRepository categoryRepository, CategoryService categoryService) {
        super(repository);
        this.productRepository = productRepository;
        this.categoryService = categoryService;
    }

    /**
     * Creates a new product in the database.
     *
     * @param productName The name of the product. Cannot be null or empty.
     * @param description The description of the product. Cannot be null or empty.
     * @param categoryId  The ID of the category this product belongs to. Cannot be null.
     * @param productImageUrl An optional URL of the product image.
     * @return The saved Product entity.
     * @throws IllegalArgumentException If any required fields are missing or invalid.
     */

    @Transactional
    public Product createProduct(String productName, String description, Long categoryId, String productImageUrl) {

        validateProductFields(productName, description, categoryId, productImageUrl);

        Category category = categoryService.findById(categoryId);

        Product productToUpdate = new Product(productName.trim(), description.trim(), productImageUrl.trim(), category);

        return productRepository.save(productToUpdate);
    }

    /**
     * Deletes a product by its ID.
     *
     * @param productId The ID of the product to delete.
     */

    @Transactional
    public void deleteProduct(Long productId) {delete(productId);}

    /**
     * Updates an existing product’s details.
     *
     * @param productId The ID of the product to update.
     * @param productName The new name of the product.
     * @param description The new description of the product.
     * @param categoryId The ID of the new category.
     * @param productImageUrl The new product image URL.
     * @return The updated Product entity.
     * @throws DuplicateResourceException If a product with the given name already exists.
     */

    @Transactional
    public Product updateProduct(Long productId, String productName, String description, Long categoryId, String productImageUrl) {

        validateProductFields(productName, description, categoryId, productImageUrl);

        //check if productName is unique
        if (productRepository.existsByProductName(productName)) {
            Product duplicateProduct = productRepository.findByProductName(productName);
            throw new DuplicateResourceException("Product with name " + productName + " already exists with id " + duplicateProduct.getId() + " in category " + duplicateProduct.getCategory() + ".");
        }

        //find category if exists
        Category category = categoryService.findById(categoryId);

        Product productToUpdate = findById(productId);

        productToUpdate.setProductName(productName.trim());
        productToUpdate.setProductDescription(description.trim());
        productToUpdate.setCategory(category);
        productToUpdate.setProductImageUrl(productImageUrl != null ? productImageUrl.trim() : null);

        return productRepository.save(productToUpdate);
    }

    /**
     * Validates the required fields of a product before saving.
     *
     * @param productName The product name to validate.
     * @param description The product description to validate.
     * @param categoryId The category ID to validate.
     * @param productImageUrl The product image URL to validate (optional).
     * @throws IllegalArgumentException If any required fields are missing or invalid.
     */

    private void validateProductFields(String productName, String description, Long categoryId, String productImageUrl) {
        if (productName == null || productName.isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (description == null || description.isEmpty()) {
            throw new IllegalArgumentException("Product description cannot be empty");
        }
        if (categoryId == null) {
            throw new IllegalArgumentException("Category cannot be null");
        }
    }

}

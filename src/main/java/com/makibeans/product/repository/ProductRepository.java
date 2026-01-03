package com.makibeans.product.repository;

import com.makibeans.category.model.Category;
import com.makibeans.product.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing `Product` entities.
 */

public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Checks if a product with the given name exists.
     *
     * @param name the name of the product
     * @return true if a product with the given name exists, false otherwise
     */

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long productId);

    /**
     * Slug-based uniqueness checks (new strategy):
     */
    boolean existsBySlug(String slug);

    boolean existsBySlugAndIdNot(String slug, Long id);

    /**
     * Slug checks constrained to non-deleted items.
     */
    boolean existsBySlugAndDeletedFalse(String slug);

    boolean existsBySlugAndIdNotAndDeletedFalse(String slug, Long id);

    Optional<Product> findBySlugAndDeletedFalse(String slug);

    /**
     * Finds products by category ID.
     *
     * @param categoryId the ID of the category
     * @return a list of products in the given category
     */

    boolean existsByCategoryId(Long categoryId);

    @Query("SELECT p FROM Product p WHERE p.category.id = :id")
    List<Product> findProductsByCategoryId(@Param("id") Long categoryId);

    // Paged search with FULL graph (category, variants+size, attributes+values)

    @EntityGraph(attributePaths = {
            "category",

            "productVariants",
            "productVariants.size",

            "productAttributes",
            "productAttributes.categoryAttribute",
            "productAttributes.categoryAttribute.attribute",

            "productAttributes.productAttributeValues",
            "productAttributes.productAttributeValues.attributeValue"
    })

    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    Boolean existsByCategoryAndDeletedFalse(Category category);

   Optional<Product> findByIdAndDeletedFalse(Long id);
}

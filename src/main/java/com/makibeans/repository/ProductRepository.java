package com.makibeans.repository;

import com.makibeans.model.Attribute;
import com.makibeans.model.Product;
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
            "productVariants", "productVariants.size",
            "productAttributes", "productAttributes.attribute",
            "productAttributes.productAttributeValues",
            "productAttributes.productAttributeValues.attributeValue"
    })
    Page<Product> findAll(Specification<Product> spec, Pageable pageable);

    boolean existsByNameAndIdNot(String name, Long productId);
}

package com.makibeans.repository;

import com.makibeans.model.Product;
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
     * @param productName the name of the product
     * @return true if a product with the given name exists, false otherwise
     */

    boolean existsByProductName(String productName);

    /**
     * Finds products by category ID.
     *
     * @param categoryId the ID of the category
     * @return a list of products in the given category
     */

    @Query("SELECT p FROM Product p WHERE p.category.id = :id")
    List<Product> findProductsByCategoryId(@Param("id") Long categoryId);
}

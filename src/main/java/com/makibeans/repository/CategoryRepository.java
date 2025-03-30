package com.makibeans.repository;

import com.makibeans.model.Category;
import com.makibeans.model.Product;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

/**
 * Repository for the category entity.
 */

public interface CategoryRepository extends JpaRepository<Category, Long> {

    /**
     * Returns the category with the given name and parent category.
     *
     * @param name The name of the category.
     * @param parentCategory The parent category.
     * @return The category with the given name and parent category.
     */

    boolean existsByNameAndParentCategory(String name, Category parentCategory);

    /**
     * Returns the products associated with the category by the given ID.
     *
     * @param id The ID of the category.
     * @return The products associated with the category.
     */

    @Query("select c FROM Category c LEFT JOIN FETCH c.products WHERE c.id = :id")
    Collection<? extends Product> findWithProductsById(@Param("id") Long id);

    /**
     * Returns the list of categories with the given parent category ID.
     *
     * @param parentCategoryId The ID of the parent category.
     * @return The list of categories with the given parent category ID.
     */

    List<Category> findByParentCategoryId(Long parentCategoryId);
}

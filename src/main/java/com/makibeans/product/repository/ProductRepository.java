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
import java.util.Set;

/**
 * Repository interface for managing `Product` entities.
 */

public interface ProductRepository extends JpaRepository<Product, Long> {


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


    @Query("""
            SELECT
            p.id as productId,
            MIN(pv.priceInCents) as minPriceInCents,
            MAX(pv.priceInCents) as maxPriceInCents
            FROM Product p
            JOIN p.productVariants pv
            WHERE p.id IN (:productIds)
            AND pv.deleted = false
            AND pv.stock > 0
                  GROUP BY p.id
            """)
    List<PriceRange> findPriceRangesForProducts(
            @Param("productIds") List<Long> productIds
    );

    //add ca.filterable
    @Query("""
                SELECT 
                a.slug AS attributeKey,
                 av.slug AS valueKey
                FROM CategoryAttribute ca
                JOIN ca.attribute a
                JOIN a.attributeValues av
                WHERE ca.category.id = :categoryId
                AND ca.filterable = true
                 AND ca.deleted = false
                  AND a.deleted = false
                  AND av.deleted = false
                  AND a.slug in :attributeKeys
                  AND av.slug in :valueKeys
            """)
    List<AttributeValuePair> findValidAttributeValuePairsForCategory(
            @Param("categoryId") Long categoryId,
            @Param("attributeKeys") Set<String> attributeKeys,
            @Param("valueKeys") Set<String> valueKeys);

}

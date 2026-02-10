package com.makibeans.productattribute.repository;

import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.product.model.Product;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.category.model.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * Repository interface for managing `ProductAttribute` entities.
 */

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    /**
     * Checks if a ProductAttribute exists by product ID and attribute template ID.
     *
     * @param productId the ID of the product
     * @param categoryAttributeId the ID of the attribute template
     * @return true if a ProductAttribute exists, false otherwise
     */

    boolean existsByProductIdAndCategoryAttributeId(Long productId, Long categoryAttributeId);

    /**
     * Deletes attribute values by attribute value ID.
     *
     * @param attributeValueId the ID of the product attribute
     */


    @Modifying
    @Query(value = "DELETE FROM product_attribute_values WHERE attribute_value_id = :attributeValueId", nativeQuery = true)
    void deleteAttributeValuesByAttributeValueId(@Param("attributeValueId") Long attributeValueId);

    /**
     * Deletes attribute values by product attribute ID.
     *
     * @param productAttributeId the ID of the product attribute
     */

    //Note required as orphanRemoval = true is set on the relationship in ProductAttribute entity
   /* @Modifying
    @Query(value = "DELETE FROM product_attribute_values WHERE product_attribute_id = :productAttributeId", nativeQuery = true)
    void deleteAttributeValuesByProductAttributeId(@Param("productAttributeId") Long productAttributeId);
*/
    /**
     * Finds ProductAttributes by attribute template ID.
     *
     * @param categoryAttributeId the ID of the attribute template
     * @return a list of ProductAttributes
     */

    List<ProductAttribute> findByCategoryAttributeId(Long categoryAttributeId);

    /**
     * Finds ProductAttributes by product ID.
     *
     * @param productId the ID of the product
     * @return a list of ProductAttributes
     */

    List<ProductAttribute> findByProductIdAndDeletedFalse(Long productId);

    /**
     * Returns all non-deleted attribute values for the given attribute ID.
     *
     * @param categoryAttributeId The ID of the attribute template.
     * @return All non-deleted attribute values for the given attribute ID.
     */
    List<ProductAttribute> findByCategoryAttributeIdAndDeletedFalse(Long categoryAttributeId);


    //count distinct product ids by attribute ue id and not deleted
    @Query("SELECT COUNT (DISTINCT pa.product.id) FROM ProductAttribute pa WHERE pa.categoryAttribute.id = :categoryAttributeId AND pa.deleted = false" )
    Long countDistinctProductsByCategoryAttributeIdAndDeletedFalse(@Param("categoryAttributeId") Long categoryAttributeId);


    @EntityGraph(attributePaths = {
            "product",
            "categoryAttribute",
            "categoryAttribute.attribute"
    })
    Page<ProductAttribute> findAll(Specification<ProductAttribute> spec, Pageable pageable);

    boolean existsByCategoryAttributeAndDeletedFalse(CategoryAttribute categoryAttribute);

    // check if any product attribute exists for a given category and not deleted
    boolean existsByCategoryAttributeCategoryAndDeletedFalse(Category category);

    List<ProductAttribute> findByProductAndDeletedFalse(Product product);
}

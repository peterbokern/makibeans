package com.makibeans.repository;

import com.makibeans.model.ProductAttribute;
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
     * @param templateId the ID of the attribute template
     * @return true if a ProductAttribute exists, false otherwise
     */

    boolean existsByProductIdAndAttributeTemplateId(Long productId, Long templateId);

    /**
     * Deletes attribute values by attribute value ID.
     *
     * @param attributeValueId the ID of the product attribute
     */

    @Modifying
    @Query(value = "DELETE FROM product_attribute_value WHERE attribute_value_id = :attributeValueId", nativeQuery = true)
    void deleteAttributeValuesByAttributeValueId(@Param("attributeValueId") Long attributeValueId);

    /**
     * Deletes attribute values by product attribute ID.
     *
     * @param productAttributeId the ID of the product attribute
     */

    @Modifying
    @Query(value = "DELETE FROM product_attribute_value WHERE product_attribute_id = :productAttributeId", nativeQuery = true)
    void deleteAttributeValuesByProductAttributeId(@Param("productAttributeId") Long productAttributeId);

    /**
     * Finds ProductAttributes by attribute template ID.
     *
     * @param templateId the ID of the attribute template
     * @return a list of ProductAttributes
     */

    List<ProductAttribute> findByAttributeTemplateId(Long templateId);

    /**
     * Finds ProductAttributes by product ID.
     *
     * @param productId the ID of the product
     * @return a list of ProductAttributes
     */

    List<ProductAttribute> findByProductId(Long productId);


}

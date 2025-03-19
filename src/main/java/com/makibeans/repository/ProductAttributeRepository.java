package com.makibeans.repository;

import com.makibeans.model.ProductAttribute;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductAttributeRepository extends JpaRepository<ProductAttribute, Long> {

    boolean existsByProductIdAndAttributeTemplateId(Long productId, Long templateId);

    @Modifying
    @Query(value = "DELETE FROM product_attribute_value WHERE product_attribute_id = :productAttributeId", nativeQuery = true)
    void deleteAttributeValuesByProductAttributeId(@Param("productAttributeId") Long productAttributeId);

}

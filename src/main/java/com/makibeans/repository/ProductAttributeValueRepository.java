package com.makibeans.repository;

import com.makibeans.model.ProductAttributeValue;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing `ProductAttributeValue` entities.
 */

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, Long> {
    /**
     * Checks whether a {@link com.makibeans.model.ProductAttributeValue} exists
     * that references the given attribute value id.
     *
     * @param attributeValueId the id of the related attribute value
     * @return true if at least one entity exists, false otherwise
     */
    boolean existsByAttributeValueId(Long attributeValueId);


    @EntityGraph(attributePaths = {
            "productAttribute",
            "productAttribute.product",   // include if you show product info
            "productAttribute.attribute", // include if you show attribute info
            "attributeValue"              // the value itself (usually needed)
    })
    Page<ProductAttributeValue> findAll(Specification<ProductAttributeValue> spec, Pageable pageable);

    boolean existsByProductAttributeIdAndAttributeValueId(Long productAttributeId, Long attributeValueId);
}

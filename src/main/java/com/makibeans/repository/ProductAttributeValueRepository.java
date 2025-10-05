package com.makibeans.repository;

import com.makibeans.model.ProductAttributeValue;
import com.makibeans.model.id.ProductAttributeValueId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Repository interface for managing `ProductAttributeValue` entities.
 */

public interface ProductAttributeValueRepository extends JpaRepository<ProductAttributeValue, ProductAttributeValueId> {
    /**
     * Checks whether a {@link com.makibeans.model.ProductAttributeValue} exists
     * that references the given attribute value id.
     *
     * @param attributeValueId the id of the related attribute value
     * @return true if at least one entity exists, false otherwise
     */
    boolean existsByAttributeValueId(Long attributeValueId);

    List<ProductAttributeValue> findAllByAttributeValueIdInAndDeletedFalse(List<Long> attributeValueIds);
}
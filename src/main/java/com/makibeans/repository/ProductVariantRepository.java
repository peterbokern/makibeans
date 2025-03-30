package com.makibeans.repository;

import com.makibeans.model.Product;
import com.makibeans.model.ProductVariant;
import com.makibeans.model.Size;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

/**
 * Repository interface for managing `ProductVariant` entities.
 */

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    /**
     * Checks if a ProductVariant exists by product and size.
     *
     * @param product the product entity
     * @param size the size entity
     * @return true if a ProductVariant exists, false otherwise
     */

    boolean existsByProductAndSize(Product product, Size size);

    /**
     * Deletes ProductVariants by size ID.
     *
     * @param sizeId the ID of the size
     */

    @Modifying
    @Transactional
    @Query("DELETE FROM ProductVariant pv WHERE pv.size.id = :sizeId")
    void deleteBySizeId(@Param("sizeId") Long sizeId);

}

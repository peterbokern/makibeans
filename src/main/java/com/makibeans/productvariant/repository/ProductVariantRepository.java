package com.makibeans.productvariant.repository;

import com.makibeans.product.model.Product;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.size.model.Size;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
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

    /**
     * Checks whether a {@link ProductVariant} exists
     * that references the given size id.
     *
     * @param sizeId the id of the related size
     * @return true if at least one entity exists, false otherwise
     */
    boolean existsBySizeId(Long sizeId);

    boolean existsBySku(String sku);

    @EntityGraph(attributePaths = {
            "product",
            "size"
    })
    Page<ProductVariant> findAll(Specification<ProductVariant> spec, Pageable pageable);

    boolean existsBySizeAndDeletedFalse(Size size);
}

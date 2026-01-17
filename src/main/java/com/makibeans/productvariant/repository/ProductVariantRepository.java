package com.makibeans.productvariant.repository;

import com.makibeans.product.model.Product;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.size.model.Size;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for managing `ProductVariant` entities.
 */

public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    /**
     * Checks if a ProductVariant exists by product and size.
     *
     * @param product the product entity
     * @param size    the size entity
     * @return true if a ProductVariant exists, false otherwise
     */

    boolean existsByProductAndSizeAndDeletedFalse(Product product, Size size);

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

    Boolean existsByProductIdAndDeletedFalseAndIsDefaultTrue(Long productId);

    List<ProductVariant> findByProductIdAndDeletedFalse(Long productId);

    List<ProductVariant> findByProductIdAndDeletedFalseOrderByIsDefaultDescIdAsc(Long productId);

    Optional<ProductVariant> findFirstByProductIdAndDeletedFalseAndIdNotOrderByIdAsc(Long productId, Long excludeId);


    @Query("SELECT DISTINCT pv.size FROM ProductVariant pv " +
            "WHERE pv.product.category.id = :categoryId AND pv.deleted = false AND pv.stock > 0 ORDER BY pv.size.name ASC")
    List<Size> findDistinctSizesByCategoryIdAndDeletedFalse(Long categoryId);


    @Query("SELECT MIN(pv.priceInCents) FROM ProductVariant pv WHERE pv.product.category.id = :categoryId AND pv.deleted = false")
    Long findMinPriceInCentsByCategoryIdAndDeletedFalse(@Param("categoryId") Long categoryId);

    @Query("SELECT MAX(pv.priceInCents) FROM ProductVariant pv WHERE pv.product.category.id = :categoryId AND pv.deleted = false")
    Long findMaxPriceInCentsByCategoryIdAndDeletedFalse(@Param("categoryId") Long categoryId);

    boolean existsByProductCategoryIdAndDeletedFalseAndStockGreaterThan(Long categoryId, Long l);

    @Modifying(clearAutomatically = true, flushAutomatically = true)
    @Query(
                    "UPDATE ProductVariant pv SET pv.isDefault = " +
                    "CASE WHEN pv.id = :productVariantId THEN true ELSE false END " +
                    "WHERE pv.product.id = :productId " +
                    "AND pv.deleted = false")
    int setDefaultProductVariant(
            @Param("productId") Long productId,
            @Param("productVariantId") Long productVariantId);

    Boolean existsByIdAndProductIdAndDeletedFalse(Long productVariantId, Long productId);
}

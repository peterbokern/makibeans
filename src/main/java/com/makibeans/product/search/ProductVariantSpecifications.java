package com.makibeans.product.search;

import com.makibeans.product.model.Product;
import com.makibeans.productvariant.model.ProductVariant;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public  class ProductVariantSpecifications {

    private  ProductVariantSpecifications() {
    }

    //Builds specification for products matching any/all product variant filters.
    //custom filter because you want to find a product where at least 1 variant matches all variant criteria
    // if using basic ProductFilters it would search individually fo each field
    public static Specification<Product> hasMatchingVariant(
            Long minPriceInCents,
            Long maxPriceInCents,
            Boolean inStock,
            String sku,
            List<Long> sizeId
    ) {
        return ((root, query, cb) -> {

            if (minPriceInCents == null && maxPriceInCents == null &&  inStock == null && sku == null)
                return cb.conjunction();

            List<Predicate> predicates = new ArrayList<>();

            if (query != null && !Long.class.equals(query.getResultType())) query.distinct(true);

            Subquery<Long> sq = query.subquery(Long.class);
            Root<ProductVariant> pv = sq.from(ProductVariant.class);

            if (minPriceInCents != null) {
                Predicate minPrice = cb.greaterThanOrEqualTo(pv.get("priceInCents"), minPriceInCents);
                predicates.add(minPrice);
            }

            if (maxPriceInCents != null) {
                Predicate maxPrice = cb.lessThanOrEqualTo(pv.get("priceInCents"), maxPriceInCents);
                predicates.add(maxPrice);
            }

            if (inStock != null && inStock) {
                Predicate isInStock = cb.greaterThan(pv.get("stock"), 0L);
                predicates.add(isInStock);
            }

            if(sku != null && !sku.isEmpty()) {
                Predicate eqSku = cb.equal(pv.get("sku"), sku.trim().toUpperCase());
                predicates.add(eqSku);
            }

            if(sizeId != null && !sizeId.isEmpty()) {
                Predicate inSizeId = pv.get("size").get("id").in(sizeId);
                predicates.add(inSizeId);
            }

            if (predicates.isEmpty()) {
                return cb.conjunction();
            }

           Predicate finalPredicate = predicates.stream().reduce(cb.conjunction(), cb::and);

            sq.select(
                            cb.literal(1L))
                    .where(
                            cb.equal(root.get("id"), pv.get("product").get("id")),
                            finalPredicate
                            );

            return cb.exists(sq);
        });
    }
}

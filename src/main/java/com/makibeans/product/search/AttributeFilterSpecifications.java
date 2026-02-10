package com.makibeans.product.search;

import com.makibeans.attribute.model.Attribute;
import com.makibeans.attributevalue.model.AttributeValue;
import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.product.model.Product;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.productattributevalue.model.ProductAttributeValue;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public final class AttributeFilterSpecifications {

    private AttributeFilterSpecifications() {}

    //"Return a product if there exists at least one attribute-value row
    //for that product where the attribute matches the given key
    //and the value matches one of the requested values."
    public static Specification<Product> hasAttributeValue(String attributeKey, List<String> attributeValueKeys) {
        return (root, query, cb) -> {

            if (query != null && !Long.class.equals(query.getResultType())) {
                query.distinct(true);
            }

            if (attributeKey == null || attributeKey.isBlank()) return cb.conjunction();
            if (attributeValueKeys == null || attributeValueKeys.isEmpty()) return cb.conjunction();

            Subquery<Long> sq = query.subquery(Long.class);

            Root<ProductAttributeValue> pav = sq.from(ProductAttributeValue.class);

            // pav --> pa
            Join<ProductAttributeValue, ProductAttribute> pa = pav.join("productAttribute");
            // pa --> p
            Join<ProductAttribute, Product> p = pa.join("product");
            // pa --> ca
            Join<ProductAttribute, CategoryAttribute> ca = pa.join("categoryAttribute");
            // ca --> a
            Join<CategoryAttribute, Attribute> a = ca.join("attribute");
            // pav --> av
            Join<ProductAttributeValue, AttributeValue> av = pav.join("attributeValue");

            sq.select(cb.literal(1L))
                    .where(
                            // correlate to outer product
                            cb.equal(p.get("id"), root.get("id")),

                            // match attribute slug
                            cb.equal(a.get("slug"), attributeKey),

                            // match value slug(s)
                            av.get("slug").in(attributeValueKeys)
                    );

            return cb.exists(sq);
        };
    }

/*
    public static Specification<Product> hasMinPrice(Long price) {

        return ((root, query, cb) -> {

            if (price == null || query == null) return cb.conjunction();

            query.distinct(true);

            Subquery<Long> sq = query.subquery(Long.class);

            Root<ProductVariant> pv = sq.from(ProductVariant.class);

            // SELECT 1 FROM  ProductVariant pv WHERE pv.id = p.id
            sq.select(
                            cb.literal(1L))
                    .where(
                            cb.equal(pv.get("product").get("id"), root.get("id")),
                            cb.greaterThanOrEqualTo(pv.get("priceInCents"), price)
                    );
            return cb.exists(sq);
        });
    }*/
}


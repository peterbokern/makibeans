package com.makibeans.attribute.productattribute.model;

import com.makibeans.attribute.categoryattribute.model.CategoryAttribute;
import com.makibeans.attribute.productattributevalue.model.ProductAttributeValue;
import com.makibeans.audit.model.Auditable;
import com.makibeans.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a product attribute entity.
 * This entity is used to store attributes for products.
 */

@Entity
@NoArgsConstructor
@Getter @Setter
@Table(name = "product_attributes", indexes = {
        @Index(name = "idx_product_attribute_product", columnList = "product_id"),
        @Index(name = "idx_product_attribute_category_attribute", columnList = "category_attribute_id")
})
@ToString(exclude = {"product"})
public class ProductAttribute extends Auditable {

    @Id @Setter(lombok.AccessLevel.NONE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_product"))
    @NotNull(message = "Product id cannot be null.")
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) // If a product is deleted, all its attributes are also deleted at the database level. This is different from JPA's orphanRemoval = true, which operates at the JPA (Java) level, not directly in the database. Your usage is appropriate for enforcing referential integrity in the database.
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(
            name = "category_attribute_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_attribute_category_attribute"))
    @NotNull(message = "CategoryAttribute id cannot be null.")
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) // If an attribute is deleted, all its product attributes are also deleted at the database level. This is different from JPA's orphanRemoval = true, which operates at the JPA (Java) level, not directly in the database. Your usage is appropriate for enforcing referential integrity in the database.
    private CategoryAttribute categoryAttribute;

    @Column(name = "visible", nullable = false)
    private Boolean visible = true;

    @OneToMany(
            mappedBy = "productAttribute",
            fetch = FetchType.LAZY,
            cascade = CascadeType.ALL,
            orphanRemoval = true) // orphanRemoval = true to ensure that when a ProductAttributeValue is removed from the collection, it is also deleted from the database.
    private Set<ProductAttributeValue> productAttributeValues = new HashSet<>();

    public ProductAttribute(CategoryAttribute categoryAttribute, Product product) {
        this.categoryAttribute = categoryAttribute;
        this.product = product;
    }
}
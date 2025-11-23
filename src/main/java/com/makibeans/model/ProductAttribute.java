package com.makibeans.model;

import com.makibeans.model.audit.Auditable;
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
@Getter
@Table(name = "product_attributes", indexes = {
        @Index(name = "idx_product_attribute_id", columnList = "product_id"),
        @Index(name = "idx_attribute_value_id", columnList = "attribute_id")
})

@ToString(exclude = {"product"})
public class ProductAttribute extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_product"))
    @NotNull(message = "Product id cannot be null.")
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) // If a product is deleted, all its attributes are also deleted at the database level. This is different from JPA's orphanRemoval = true, which operates at the JPA (Java) level, not directly in the database. Your usage is appropriate for enforcing referential integrity in the database.
    private Product product;

    @Setter
    @ManyToOne
    @JoinColumn(name = "attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_attribute"))
    @NotNull(message = "Attribute id cannot be null.")
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) // If an attribute is deleted, all its product attributes are also deleted at the database level. This is different from JPA's orphanRemoval = true, which operates at the JPA (Java) level, not directly in the database. Your usage is appropriate for enforcing referential integrity in the database.
    private Attribute attribute;

    @OneToMany(
            mappedBy = "productAttribute",
            cascade = CascadeType.ALL, //cascade all operations to attribute values
            orphanRemoval = true, //remove attribute values when they are no longer referenced
            fetch = FetchType.LAZY) //only load attribute values when needed
    private Set<ProductAttributeValue> productAttributeValues = new HashSet<>();

    public ProductAttribute(Attribute attribute, Product product) {
        this.attribute = attribute;
        this.product = product;
    }
}
package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product attribute entity.
 * This entity is used to store attributes for products.
 */

@Entity
@NoArgsConstructor
@Getter
@Table(name = "product_attributes", indexes = {
        @Index(name = "idx_product_attribute_id", columnList = "product_id"),
        @Index(name = "idx_attribute_value_id", columnList = "template_id")
})

@ToString(exclude = {"product", "attributeValues"})
public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @NotNull(message = "Product id cannot be null.")
    private Product product;

    @Setter
    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    @NotNull(message = "Template id cannot be null.")
    private AttributeTemplate attributeTemplate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_attribute_values",
            joinColumns = @JoinColumn(name = "product_attribute_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id"),
            uniqueConstraints = @UniqueConstraint(columnNames = {"product_attribute_id", "attribute_value_id"})
    )
    private  List<AttributeValue> attributeValues = new ArrayList<>();

    public ProductAttribute(AttributeTemplate attributeTemplate, Product product) {
        this.attributeTemplate = attributeTemplate;
        this.product = product;
    }
}
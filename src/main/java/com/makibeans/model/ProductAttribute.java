package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "product_attribute", indexes = {
        @Index(name = "idx_product_attribute_id", columnList = "product_id"),
        @Index(name = "idx_attribute_value_id", columnList = "template_id")
})

public class ProductAttribute {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Setter
    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private AttributeTemplate attributeTemplate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "product_attribute_value",
            joinColumns = @JoinColumn(name = "product_attribute_id"),
            inverseJoinColumns = @JoinColumn(name = "attribute_value_id")
    )
    private  List<AttributeValue> attributeValues = new ArrayList<>();

    public ProductAttribute(AttributeTemplate attributeTemplate, Product product) {
        this.attributeTemplate = attributeTemplate;
        this.product = product;
    }

    public void addAttributeValue(AttributeValue attributeValue) {
        attributeValues.add(attributeValue);
    }

    public void removeAttributeValue(AttributeValue attributeValue) {
        attributeValues.remove(attributeValue);
    }

    @Override
    public String toString() {
        return "ProductAttribute{" +
                "attributeTemplate=" + attributeTemplate.getName() +
                ", id=" + id +
                ", product=" + product +
                ", attributeValues=" + attributeValues +
                '}';
    }
}
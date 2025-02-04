package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter


public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Setter
    @Column(name = "product_name", unique = true, nullable = false, length = 100)
    String productName;

    @Setter
    @Column(name = "product_description", nullable = false, length = 1000)
    String productDescription;

    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    Category category;

    @Setter
    @Column(name = "product_image_url", nullable = true, length = 1000)
    String productImageUrl;

    //Ensures that adding & removing attributes and variants will be cascaded to the database
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> productVariants = new ArrayList<>();

    public Product(String productName, String productDescription, String productImageUrl, Category category) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
        this.category = category;
    }

    public void addProductAttribute(ProductAttribute productAttribute) {
        productAttributes.add(productAttribute);
        productAttribute.setProduct(this);
    }

    public void removeProductAttribute(ProductAttribute productAttribute) {
        productAttributes.remove(productAttribute);
        productAttribute.setProduct(null);
    }

    public void addProductVariant(ProductVariant productVariant) {
        productVariants.add(productVariant);
        productVariant.setProduct(this);
    }

    public void removeProductVariant(ProductVariant productVariant) {
        productVariants.remove(productVariant);
        productVariant.setProduct(null);
    }

    @Override
    public String toString() {
        return "Product{" +
                "id=" + id +
                ", productName='" + productName + '\'' +
                ", productDescription='" + productDescription + '\'' +
                ", productImageUrl='" + productImageUrl + '\'' +
                ", productAttributes=" + productAttributes +
                ", productVariants=" + productVariants +
                '}';
    }
}
package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@ToString(exclude = {"productAttributes", "category", "productVariants"})
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Setter
    @Column(name = "product_name", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Product name cannot be blank.")
    String productName;

    @Setter
    @Column(name = "product_description", nullable = false, length = 1000)
    @NotBlank(message = "Product description cannot be blank.")
    String productDescription;

    @Setter
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category cannot be null.")
    Category category;

    @Setter
    @Column(name = "product_image_url", nullable = true, length = 1000)
    String productImageUrl;

    //Ensures that adding & removing attributes and variants will be cascaded to the database
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> productVariants = new ArrayList<>();

    @Builder
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
}
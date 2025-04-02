package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product in the system.
 * A product can have multiple attributes, variants, and images.
 */

@Entity
@NoArgsConstructor
@Getter
@ToString(exclude = {"productAttributes", "category", "productVariants", "productImage"})

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category cannot be null.")
    Category category;

    @Setter
    @Column(name = "product_image_url", nullable = true, length = 1000)
    String productImageUrl;

    @Setter
    @Lob
    @Column(name = "product_image", nullable = true)
    private byte[] productImage;

    //Ensures that adding & removing attributes and variants will be cascaded to the database
    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<ProductVariant> productVariants = new ArrayList<>();

    @Builder
    public Product(String productName,
                   String productDescription,
                   String productImageUrl,
                     byte[] productImage,
                   Category category) {
        this.productName = productName;
        this.productDescription = productDescription;
        this.productImageUrl = productImageUrl;
        this.productImage = productImage;
        this.category = category;
    }
}
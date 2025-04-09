package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a product in the system.
 * A product can have multiple attributes, variants, and images.
 */

@Entity
@NoArgsConstructor
@Getter
@ToString(exclude = {"productAttributes", "category", "productVariants", "image"})
@Table(name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_description", columnList = "description")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"})
        })

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Setter
    @Column(name = "name", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Product name cannot be blank.")
    String name;

    @Setter
    @Column(name = "description", nullable = false, length = 1000)
    @NotBlank(message = "Product description cannot be blank.")
    String description;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false)
    @NotNull(message = "Category cannot be null.")
    Category category;

    @Setter
    @Lob
    @Column(name = "image", nullable = true)
    private byte[] image;

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
    public Product(String name,
                   String description,
                     byte[] image,
                   Category category) {
        this.name = name;
        this.description = description;
        this.image = image;
        this.category = category;
    }
}
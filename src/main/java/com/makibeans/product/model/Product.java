package com.makibeans.product.model;

import com.makibeans.category.model.Category;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.audit.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a product in the system.
 * A product can have multiple attributes, variants, and images.
 *
 * Additional notes:
 * - A URL-friendly `slug` is stored and used for uniqueness checks and public lookups.
 * - The `name` is left for display, while `slug` is used for stable unique identification.
 */

@Entity
@NoArgsConstructor
@Getter
@Setter
@ToString(exclude = {"productAttributes", "category", "productVariants", "image"})
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_description", columnList = "description"),
                @Index(name = "idx_product_slug", columnList = "slug")
        })

public class Product extends Auditable {
    @Id @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", nullable = false, length = 100)
    @NotBlank(message = "Product name cannot be blank.")
    String name;

    @Column(name = "description", nullable = false, length = 1000)
    @NotBlank(message = "Product description cannot be blank.")
    String description;

    /**
     * URL-friendly identifier derived from name (e.g. "red-coffee-beans").
     * Used for uniqueness checks and public lookup.
     */
    @Column(name = "slug", unique = true, nullable = false, length = 120)
    private String slug;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
    @NotNull(message = "Category cannot be null.")
    Category category;

    @Lob
    @Column(name = "image", nullable = true)
    private byte[] image;

    //Ensures that adding & removing attributes and variants will be cascaded to the database
    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY)
    private Set<ProductAttribute> productAttributes = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            fetch = FetchType.LAZY)
    private Set<ProductVariant> productVariants = new HashSet<>();

    @Builder
    public Product(String name,
                   String description,
                     String slug,
                     byte[] image,
                   Category category) {
        this.name = name;
        this.slug = slug;
        this.description = description;
        this.image = image;
        this.category = category;
    }
}
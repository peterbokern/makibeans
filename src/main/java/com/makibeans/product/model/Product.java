package com.makibeans.product.model;

import com.makibeans.category.model.Category;
import com.makibeans.productattribute.model.ProductAttribute;
import com.makibeans.productvariant.model.ProductVariant;
import com.makibeans.audit.model.Auditable;
import com.makibeans.util.TextUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

/**
 * Represents a product in the system.
 * A product can have multiple attributes, variants, and images.
 */

@Entity
@NoArgsConstructor
@Getter
@ToString(exclude = {"productAttributes", "category", "productVariants", "image"})
@Table(
        name = "products",
        indexes = {
                @Index(name = "idx_product_name", columnList = "name"),
                @Index(name = "idx_product_description", columnList = "description")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name"})
        })

public class Product extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "name", unique = true, nullable = false, length = 100)
    @NotBlank(message = "Product name cannot be blank.")
    String name;

    @Column(name = "description", nullable = false, length = 1000)
    @NotBlank(message = "Product description cannot be blank.")
    String description;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_category"))
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
    private Set<ProductAttribute> productAttributes = new HashSet<>();

    @OneToMany(
            mappedBy = "product",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private Set<ProductVariant> productVariants = new HashSet<>();

    @Builder
    public Product(String name,
                   String description,
                     byte[] image,
                   Category category) {
        this.setName(name);
        this.setDescription(description);
        this.image = image;
        this.category = category;
    }

    public void setName(String name) {
        this.name = TextUtils.trim(name);
    }

    public void setDescription(String description) {
        this.description = TextUtils.trim(description);
    }
}
package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents a category in the system.
 * A category can have a parent category and multiple subcategories.
 * It can also contain multiple products.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(
        name = "category",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "parent_category_id"})
        },
        indexes = {
                @Index(name = "idx_category_name", columnList = "name"),
                @Index(name = "idx_category_description", columnList = "description")
        }
)

@ToString(exclude = {"parentCategory", "subCategories", "image", "products"})
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Category name cannot be blank.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Setter
    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Setter
    @Lob
    @Column(name = "image", nullable = true)
    private byte[] image;

    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_category_id", nullable = true)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory",
            cascade = CascadeType.ALL,
            orphanRemoval = true,
            fetch = FetchType.LAZY)
    private List<Category> subCategories = new ArrayList<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY)
    private List<Product> products = new ArrayList<>();

    public Category(String name, String description) {
        this.name = name;
        this.description = description;
    }
}


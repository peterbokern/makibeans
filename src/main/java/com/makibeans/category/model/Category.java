package com.makibeans.category.model;

import com.makibeans.categoryattribute.model.CategoryAttribute;
import com.makibeans.audit.model.Auditable;
import com.makibeans.product.model.Product;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

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
        name = "categories",
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"name", "parent_category_id"})
        },
        indexes = {
                @Index(name = "idx_category_name", columnList = "name"),
                @Index(name = "idx_category_description", columnList = "description")
        }
)

@ToString(exclude = {"parentCategory", "subCategories", "image", "products"})
public class Category extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Category name cannot be blank.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, length = 60)
    private String slug;

    @Column(name = "description", nullable = true, length = 1000)
    private String description;

    @Setter
    @Lob
    @Column(name = "image", nullable = true)
    private byte[] image;

    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_category_id", nullable = true, foreignKey = @ForeignKey(name = "fk_category_parent_category"))
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory",
            fetch = FetchType.LAZY)
    private Set<Category> subCategories = new HashSet<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY)
    private Set<Product> products = new HashSet<>();

    @OneToMany(
            mappedBy = "category",
            fetch = FetchType.LAZY)
    private Set<CategoryAttribute> attributes = new HashSet<>(
    );

    public Category(String name, String description) {
        this.setName(name);
        this.setDescription(description);
    }
}

package com.makibeans.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "category", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name", "parent_category_id"})})

@ToString(exclude = {"parentCategory", "subCategories", "products"})
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "id") // 🚀 Prevent infinite loop
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
    @Column(name = "image_url", nullable = true, length = 1000)
    private String imageUrl;

    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_category_id", nullable = true)
    private Category parentCategory;

    //Delete all subcategories of parent category when removed
    @OneToMany(mappedBy = "parentCategory", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Product> products = new ArrayList<>();


    public Category(String name, String description, String imageUrl) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
    }

    public Category(String name, String description, String imageUrl, Category parentCategory) {
        this.name = name;
        this.description = description;
        this.imageUrl = imageUrl;
        this.parentCategory = parentCategory;
    }

    public void addProduct(Product product) {
        products.add(product);
        product.setCategory(this);
    }

    public void removeProduct(Product product) {
        products.remove(product);
        product.setCategory(null);
    }

    public void addSubCategory(Category category) {
        subCategories.add(category);
        category.setParentCategory(this);
    }

    public void removeSubCategory(Category category) {
        subCategories.remove(category);
        category.setParentCategory(null);
    }
}

package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "category")
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Setter
    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Setter
    @Column(name = "image_url", nullable = true, length = 1000)
    private String imageUrl;

    @Setter
    @ManyToOne
    @JoinColumn(name = "parent_category_id", nullable = true)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private final List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Product> products = new ArrayList<>();

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

    @Override
    public String toString() {
        return "Category{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", imageUrl='" + imageUrl + '\'' +
                ", parentCategory=" + parentCategory.getName() +
                ", subCategories=" + subCategories +
                ", products=" + products +
                '}';
    }



}

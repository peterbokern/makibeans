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

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false, length = 1000)
    private String description;

    @Column(name = "image_url", nullable = true, length = 1000)
    private String imageUrl;

    @ManyToOne
    @JoinColumn(name = "parent_category_id", nullable = true)
    private Category parentCategory;

    @OneToMany(mappedBy = "parentCategory")
    private final List<Category> subCategories = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Product> products = new ArrayList<>();



}

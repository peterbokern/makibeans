package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter

public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(name = "product_name", nullable = false, length = 100)
    String productName;

    @Column(name = "product_description", nullable = false, length = 1000)
    String productDescription;

    @Column(name = "product_image_url", nullable = true, length = 1000)
    String productImageUrl;

    //Ensures that adding & removing attributes and variants will be cascaded to the database
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductAttribute> productAttributes = new ArrayList<>();

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ProductVariant> productVariants = new ArrayList<>();






}
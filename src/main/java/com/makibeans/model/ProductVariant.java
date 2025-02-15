package com.makibeans.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@NoArgsConstructor
@Getter
@Entity
@ToString(exclude = {"product", "size"})
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Setter
    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Setter
    @Column(name = "price_in_cents", nullable = false, length = 100)
    private Long priceInCents;

    @Setter
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Setter
    @Column(name = "stock", nullable = false, length = 100)
    private Long stock;

    public ProductVariant(Product product, Size size, Long priceInCents, String sku, Long stock) {
        this.product = product;
        this.size = size;
        this.priceInCents = priceInCents;
        this.sku = sku;
        this.stock = stock;
    }
}
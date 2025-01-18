package com.makibeans.model;

import com.makibeans.model.AttributeValue;
import com.makibeans.model.Product;
import jakarta.persistence.*;

@Entity
public class ProductVariant {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    // size will determine product price --TODO add constraint to ensure "Size" is referenced"
    @ManyToOne
    @JoinColumn(name = "attribute_value_id", nullable = false)
    private AttributeValue size;

    @Column(name = "price_in_cents", nullable = false, length = 100)
    private Long priceInCents;

    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Column(name = "stock", nullable = false, length = 100)
    private Long stock;

    @Override
    public String toString() {
        return "ProductVariant{" +
                "id=" + id +
                ", product=" + product +
                ", size=" + size +
                ", priceInCents=" + priceInCents +
                ", sku='" + sku + '\'' +
                ", stock=" + stock +
                '}';
    }
}
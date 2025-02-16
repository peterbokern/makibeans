package com.makibeans.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Product cannot be null.")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Setter
    @NotNull(message = "Size cannot be null.")
    @ManyToOne
    @JoinColumn(name = "size_id", nullable = false)
    private Size size;

    @Setter
    @NotNull(message = "Price cannot be null.")
    @Min(value = 0, message = "Price should be a minimum of 0.")
    @Column(name = "price_in_cents", nullable = false)
    private Long priceInCents;

    @Setter
    @NotBlank(message = "SKU cannot be blank.")
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Setter
    @NotNull(message = "Stock cannot be null.")
    @Min(value = 0, message = "Stock should be a minimum of 0.")
    @Column(name = "stock", nullable = false)
    private Long stock;

    public ProductVariant(Product product, Size size, Long priceInCents, String sku, Long stock) {
        this.product = product;
        this.size = size;
        this.priceInCents = priceInCents;
        this.sku = sku;
        this.stock = stock;
    }
}
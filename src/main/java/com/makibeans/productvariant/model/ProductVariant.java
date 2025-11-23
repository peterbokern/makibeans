package com.makibeans.productvariant.model;
import com.makibeans.audit.model.Auditable;
import com.makibeans.product.model.Product;
import com.makibeans.size.model.Size;
import com.makibeans.util.TextUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents a product variant entity.
 * This entity is used to store variants for products.
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
@ToString(exclude = {"product", "size"})
@Table(name = "product_variants",
        indexes = {
                @Index(name = "idx_product_variant_price", columnList = "price_in_cents")
        },
        uniqueConstraints = {
                @UniqueConstraint(columnNames = {"sku"})
        })
public class ProductVariant extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotNull(message = "Product cannot be null.")
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_variant_product"))
    @OnDelete(action = OnDeleteAction.CASCADE) // If a product is deleted, all its variants are also deleted at the database level. This is different from JPA's orphanRemoval = true, which operates at the JPA (Java) level, not directly in the database. Your usage is appropriate for enforcing referential integrity in the database.
    private Product product;

    @Setter
    @NotNull(message = "Size cannot be null.")
    @ManyToOne
    @OnDelete(action = OnDeleteAction.RESTRICT) // Prevent deletion of a Size if it is referenced by any ProductVariant
    @JoinColumn(name = "size_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_variant_size"))
    private Size size;

    @Setter
    @NotNull(message = "Price cannot be null.")
    @Min(value = 0, message = "Price should be a minimum of 0.")
    @Digits(integer = 10, fraction = 0, message = "Price must be a valid number with up to 10 digits.")
    @Column(name = "price_in_cents", nullable = false)
    private Long priceInCents;

    @NotBlank(message = "SKU cannot be blank.")
    @Column(name = "sku", nullable = false, length = 100)
    private String sku;

    @Setter
    @NotNull(message = "Stock cannot be null.")
    @Min(value = 0, message = "Stock should be a minimum of 0.")
    @Digits(integer = 10, fraction = 0, message = "Stock must be a valid number with up to 10 digits.")
    @Column(name = "stock", nullable = false)
    private Long stock;

    public ProductVariant(Product product, Size size, Long priceInCents, String sku, Long stock) {
        this.product = product;
        this.size = size;
        this.priceInCents = priceInCents;
        this.setSku(sku);
        this.stock = stock;
    }

    public void setSku(String sku) {
        this.sku = TextUtils.normalizeText(sku);
    }
}
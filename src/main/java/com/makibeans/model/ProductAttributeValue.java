package com.makibeans.model;

import com.makibeans.model.audit.Auditable;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents the mapping between a product attribute and its value.
 * Using a surrogate Long id simplifies API and repository usage while keeping
 * a unique constraint on (product_attribute_id, attribute_value_id).
 */

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Table(name = "product_attribute_values",
        uniqueConstraints = @UniqueConstraint(columnNames = {"product_attribute_id","attribute_value_id"}))
public class ProductAttributeValue extends Auditable {

    public ProductAttributeValue(AttributeValue attributeValue, ProductAttribute productAttribute) {
        this.attributeValue = attributeValue;
        this.productAttribute = productAttribute;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_value_product_attribute"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    private ProductAttribute productAttribute;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attribute_value_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_value_attribute_value"))
    @OnDelete(action = OnDeleteAction.RESTRICT)
    private AttributeValue attributeValue;
}

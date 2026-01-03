package com.makibeans.attribute.productattributevalue.model;

import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.audit.model.Auditable;
import com.makibeans.attribute.productattribute.model.ProductAttribute;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

//TODO add unique constraint in database

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
@Table(name = "product_attribute_values", indexes = {
        @Index(name = "idx_product_attribute_value_product_attribute", columnList = "product_attribute_id"),
        @Index(name = "idx_product_attribute_value_attribute_value", columnList = "attribute_value_id")
})
public class ProductAttributeValue extends Auditable {

    // Constructor for ENUM / DROPDOWN / MULTISELECT input types
    public ProductAttributeValue(ProductAttribute productAttribute, AttributeValue attributeValue) {
        this.productAttribute = productAttribute;
        this.attributeValue = attributeValue;
        this.rawValue = null;
    }

    // Constructor for FREE_TEXT / NUMERIC / DATE_PICKER / SLIDER / CHECKBOX / BOOLEAN input types
    public ProductAttributeValue(ProductAttribute productAttribute, String rawValue) {
        this.productAttribute = productAttribute;
        this.rawValue = rawValue;
        this.attributeValue = null;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "product_attribute_id",
            nullable = false,
            foreignKey = @ForeignKey(name = "fk_product_attribute_value_product_attribute"))
    @OnDelete(action = OnDeleteAction.CASCADE)
    // If a product attribute is deleted, all its attribute values are also deleted at the database level.
    private ProductAttribute productAttribute;

    // INPUT TYPE: ENUM / DROPDOWN / MULTISELECT
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "attribute_value_id",
            nullable = true,
            foreignKey = @ForeignKey(name = "fk_product_attribute_value_attribute_value"))
    private AttributeValue attributeValue;

    //INIPUT TYPE: FREE_TEXT / NUMERIC / DATE_PICKER / SLIDER / CHECKBOX / BOOLEAN
    @Column(name = "raw_value", nullable = true, length = 255)
    private String rawValue;
}

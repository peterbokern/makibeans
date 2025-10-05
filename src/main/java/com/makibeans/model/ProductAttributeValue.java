package com.makibeans.model;

import com.makibeans.model.audit.Auditable;
import com.makibeans.model.id.ProductAttributeValueId;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents the mapping between a product attribute and its value.
 * This entity uses a composite primary key defined by {@link ProductAttributeValueId}.
 * The combination of product attribute and attribute value must be unique.
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
        this.id = new ProductAttributeValueId(productAttribute.getId(), attributeValue.getId());
        this.attributeValue = attributeValue;
        this.productAttribute = productAttribute;
    }

    /**
     * Composite primary key for the ProductAttributeValue entity.
     * This field is annotated with @EmbeddedId, indicating that it is a composite key.
     * The @MapsId annotations on the related fields (productAttribute and attributeValue)
     * map the fields of this composite key to the corresponding columns in the database.
     */
    @EmbeddedId
    private ProductAttributeValueId id;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("productAttributeId")
    @JoinColumn(name = "product_attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_value_product_attribute"))
    @OnDelete(action = org.hibernate.annotations.OnDeleteAction.CASCADE) // Delete product attribute value if product attribute is deleted. Cascade delete to child records on DB level. This is different from orphanRemoval = true which is JPA level
    private ProductAttribute productAttribute;

    @ManyToOne(fetch = FetchType.LAZY) @MapsId("attributeValueId")
    @JoinColumn(name = "attribute_value_id", nullable = false, foreignKey = @ForeignKey(name = "fk_product_attribute_value_attribute_value"))
    @OnDelete(action = OnDeleteAction.RESTRICT) // prevent deletion of product attribute value if attribute value is deleted. Restrict delete on DB level.
    private AttributeValue attributeValue;
}

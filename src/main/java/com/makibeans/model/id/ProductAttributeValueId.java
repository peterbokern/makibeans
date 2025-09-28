package com.makibeans.model.id;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

/**
 * Composite primary key class for the ProductAttributeValue entity.
 * Represents the combination of product attribute ID and attribute value ID.
 */

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Embeddable
public class ProductAttributeValueId implements Serializable {
    @Column(name = "product_attribute_id")
    private long productAttributeId;

    @Column(name = "attribute_value_id")
    private long attributeValueId;
}

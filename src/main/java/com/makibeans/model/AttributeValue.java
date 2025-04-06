package com.makibeans.model;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Represents an attribute value entity.
 * This entity is used to store values for attributes defined by an AttributeTemplate.
 */

@Entity
@Getter
@NoArgsConstructor
@Table(name = "attribute_values", indexes = {
        @Index(name = "attribute_value_template_id_index", columnList = "template_id"),
        @Index(name = "idx_attribute_value_value", columnList = "value")
})

@ToString(exclude = "attributeTemplate")
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @Setter
    @JoinColumn(name = "template_id", nullable = false)
    private AttributeTemplate attributeTemplate;

    @Setter
    @Column(name = "value", nullable = false, length = 255)
    @NotBlank(message = "Attribute value cannot be blank.")
    @Size(min = 1, max = 255, message = "Attribute value must be between 1 and 255 characters.")
    private String value;

    public AttributeValue(AttributeTemplate attributeTemplate, String value) {
        this.attributeTemplate = attributeTemplate;
        this.value = value;
    }
}
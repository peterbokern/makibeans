package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.util.List;

/**
 * Represents an attribute template entity.
 * This entity is used to define a template for attributes that can be associated with other entities.
 */

@Entity
@Table(
        name = "attribute_templates",
        indexes = {@Index(name = "idx_attribute_template_name", columnList = "name")})
@NoArgsConstructor
@Getter
@ToString(exclude = "attributeValues")
public class AttributeTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Name of attribute template cannot be blank.")
    @Size(min = 3, max = 50, message = "Name of attribute template must be between 3 and 50 characters.")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(
            mappedBy = "attributeTemplate",
            cascade = CascadeType.REMOVE, //remove all dependent attribute values
            orphanRemoval = true,
            fetch = FetchType.LAZY) //only load attribute values when needed
    private List<AttributeValue> attributeValues;

    public AttributeTemplate(String name) {
        this.name = name;
    }
}

package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
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
        name = "attribute_template",
        indexes = {@Index(name = "idx_attribute_template_name", columnList = "name")})
@NoArgsConstructor
@Getter
@ToString
public class AttributeTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Name of attribute template cannot be blank.")
    @Column(name = "name", nullable = false, unique = true)
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

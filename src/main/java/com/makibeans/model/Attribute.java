package com.makibeans.model;

import com.makibeans.model.base.Auditable;
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
        name = "attributes",
        indexes = {@Index(name = "idx_attribute_name", columnList = "name")})
@NoArgsConstructor
@Getter
@ToString(exclude = "attributeValues")
public class Attribute extends Auditable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Name of attribute cannot be blank.")
    @Size(min = 3, max = 50, message = "Name of attribute  must be between 3 and 50 characters.")
    @Column(name = "name", nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(
            mappedBy = "attribute",
            cascade = CascadeType.REMOVE, //remove all dependent attribute values
            orphanRemoval = true,
            fetch = FetchType.LAZY) //only load attribute values when needed
    private List<AttributeValue> attributeValues;

    public Attribute(String name) {
        this.name = name;
    }
}

package com.makibeans.attribute.attribute.model;

import com.makibeans.attribute.attributevalue.model.AttributeValue;
import com.makibeans.audit.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.Set;

/**
 * Represents an attribute template entity.
 * This entity is used to define a template for attributes that can be associated with other entities.
 */

@Entity
@Table(
        name = "attributes",
        indexes = {@Index(name = "idx_attribute_name", columnList = "name")
        })
@NoArgsConstructor
@Getter @Setter
@ToString(exclude = "attributeValues")
public class Attribute extends Auditable {
    @Id @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Name of attribute cannot be blank.")
    @Size(min = 3, max = 50, message = "Name of attribute  must be between 3 and 50 characters.")
    @Column(name = "name", nullable = false, length = 50)
    private String name; //TODO create partial uniqe constrainst in flyway sql file

    @Size(max = 255, message = "Description of attribute must be less than 255 characters.")
    @Column(name = "description", length = 255)
    private String description;

    @Column(name = "slug", nullable = false, length = 60) //uniqe constraint set in db
    private String slug;

    @NotNull(message = "Data type of attribute cannot bee null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "data_type", nullable = false, length = 20)
    private AttributeDataType dataType;

    @NotNull(message = "Input type of attribute cannot bee null.")
    @Enumerated(EnumType.STRING)
    @Column(name = "input_type", nullable = false, length = 20)
    private AttributeInputType inputType;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    @OneToMany(mappedBy = "attribute")
    private Set<AttributeValue> attributeValues;
}

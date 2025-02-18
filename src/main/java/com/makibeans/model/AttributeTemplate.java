package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(
        name = "attribute_template", indexes = {
        @Index(name = "idx_attribute_template_name", columnList = "name")
})
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

    public AttributeTemplate(String name) {
        this.name = name;
    }

}

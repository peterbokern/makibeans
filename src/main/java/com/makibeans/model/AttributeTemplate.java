package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "attribute_template", indexes = {
        @Index(name = "idx_attribute_template_name", columnList = "name")
})
@NoArgsConstructor
@Getter
@Setter
public class AttributeTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, unique = true)
    private String name;

    public AttributeTemplate(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "AttributeTemplate{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
}
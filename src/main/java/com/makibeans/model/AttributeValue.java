package com.makibeans.model;

import jakarta.annotation.Nonnull;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.Setter;
import org.antlr.v4.runtime.misc.NotNull;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attribute_value", indexes = {
        @Index(name = "attribute_value_template_id_index", columnList = "template_id")
})
public class AttributeValue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id", nullable = false)
    private AttributeTemplate attributeTemplate;

    private String value;

    @Override
    public String toString() {
        return "AttributeValue{" +
                "attributeTemplate=" + attributeTemplate +
                ", id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
package com.makibeans.model;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Entity
@Getter
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

    @Setter
    @Column(name = "value", nullable = false, length = 1000)
    private String value;

    public AttributeValue(AttributeTemplate attributeTemplate, String value) {
        this.attributeTemplate = attributeTemplate;
        this.value = value;
    }

    @Override
    public String toString() {
        return "AttributeValue{" +
                "attributeTemplate=" + attributeTemplate.getName() +
                ", id=" + id +
                ", value='" + value + '\'' +
                '}';
    }
}
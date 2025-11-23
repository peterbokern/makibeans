package com.makibeans.model;
import com.makibeans.model.audit.Auditable;
import com.makibeans.util.TextUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * Represents an attribute value entity.
 * This entity is used to store values for attributes defined by an Attribute.
 */

@Entity
@Getter
@NoArgsConstructor
@Table(name = "attribute_values", indexes = {
        @Index(name = "attribute_value_id_index", columnList = "attribute_id"),
        @Index(name = "idx_attribute_value", columnList = "value")
})

@ToString(exclude = "attribute")
public class AttributeValue extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @Setter
    @JoinColumn(name = "attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attribute_value_attribute"))
    @OnDelete(action = OnDeleteAction.CASCADE) //if attribute is deleted, delete all its values
    @NotNull(message = "Attribute cannot be null.")
    private Attribute attribute;

    @Column(name = "value", nullable = false, length = 255)
    @NotBlank(message = "Attribute value cannot be blank.")
    @Size(min = 1, max = 255, message = "Attribute value must be between 1 and 255 characters.")
    private String value;

    public AttributeValue(Attribute attribute, String value) {
        this.attribute = attribute;
        this.value = value;
    }

    public void setValue(String value) {
       this.value = TextUtils.normalizeText(value);
    }
}
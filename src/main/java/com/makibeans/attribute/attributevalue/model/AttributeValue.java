package com.makibeans.attribute.attributevalue.model;
import com.makibeans.attribute.attribute.model.Attribute;
import com.makibeans.attribute.attributevalue.validation.annotation.ValidAttributeValueForType;
import com.makibeans.audit.model.Auditable;
import com.makibeans.common.util.TextUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Represents an attribute value entity.
 * This entity is used to store values for attributes defined by an Attribute.
 */

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "attribute_values", indexes = {
        @Index(name = "attribute_value_id_index", columnList = "attribute_id"),
        @Index(name = "idx_attribute_string_value", columnList = "string_value"),
        @Index(name = "idx_attribute_numeric_value", columnList = "numeric_value"),
        @Index(name = "idx_attribute_boolean_value", columnList = "boolean_value"),
        @Index(name = "idx_attribute_date_value", columnList = "date_value"),
        @Index(name = "idx_attribute_date_time_value", columnList = "date_time_value")
})
@ToString(exclude = "attribute")
@ValidAttributeValueForType // Custom validation to ensure  1  value matches attribute data type
public class AttributeValue extends Auditable {

    @Id @Setter(AccessLevel.NONE) //TODO: apply to all id fields
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch= FetchType.LAZY)
    @JoinColumn(name = "attribute_id", nullable = false, foreignKey = @ForeignKey(name = "fk_attribute_value_attribute"))
    @OnDelete(action = OnDeleteAction.CASCADE) //if attribute is deleted, delete all its values
    @NotNull(message = "Attribute cannot be null.")
    private Attribute attribute;

    @Column(name = "string_value",  length = 255)
    @Size(max = 255, message = "Attribute value must be between 1 and 255 characters.")
    private String stringValue;

    @Digits(integer = 20, fraction = 6, message = "Numeric value must be a valid number with up to 20 digits and 6 decimal places.")
    @Column(name = "numeric_value")
    private BigDecimal numericValue;

    @Column(name = "boolean_value", nullable = true)
    private Boolean booleanValue;

    @Column(name = "date_value", nullable = true)
    private LocalDate dateValue;

    @Column(name = "date_time_value", nullable = true)
    private LocalDateTime dateTimeValue;

    @Column(name = "slug", nullable = false, unique = true, length = 60)
    private String slug;

    @Column(name = "sort_order", nullable = false)
    @Min(value = 0, message = "Sort order must be zero or a positive integer.")
    private Integer sortOrder = 0;

    @Column(name = "active", nullable = false)
    private boolean active = true;

    public Object getValue() {
        return switch (this.attribute.getDataType()) {
            case STRING -> this.stringValue;
            case NUMERIC -> this.numericValue;
            case BOOLEAN -> this.booleanValue;
            case DATE -> this.dateValue;
            case DATETIME -> this.dateTimeValue;
            default -> null;
        };
    }
    public String getValueAsString() {
        Object value = getValue();
        if (value == null) return null;
        if (value instanceof BigDecimal bd) return bd.stripTrailingZeros().toPlainString();
        return value.toString();
    }
}
package com.makibeans.categoryattribute.model;

import com.makibeans.attribute.model.Attribute;
import com.makibeans.category.model.Category;
import com.makibeans.audit.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(
        name = "category_attributes",
        uniqueConstraints = {
                @UniqueConstraint(name = "uk_category_attribute", columnNames = {"category_id", "attribute_id"})
        },
        indexes = {
                @Index(name = "idx_cat_attr_category", columnList = "category_id"),
                @Index(name = "idx_cat_attr_attribute", columnList = "attribute_id")
        }
)
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor @Builder
public class CategoryAttribute extends Auditable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "category_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cat_attr_category"))
    private Category category;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "attribute_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_cat_attr_attribute"))
    private Attribute attribute;

    // Optional per-category settings (nice to have)
    @Column(name = "required", nullable = false)
    @Builder.Default
    private boolean required = false;

}

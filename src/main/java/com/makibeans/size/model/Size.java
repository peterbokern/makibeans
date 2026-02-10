package com.makibeans.size.model;

import com.makibeans.audit.model.Auditable;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

/**
 * Entity representing a Size.
 */

@Entity
@NoArgsConstructor
@Getter
@Setter
@Table(name = "sizes",
        indexes = {
                @Index(name = "idx_size_name", columnList = "name"),
                @Index(name = "idx_size_slug", columnList = "slug")
        })
@ToString
public class Size extends Auditable {

    @Id
    @Setter(AccessLevel.PRIVATE)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Size name should not be blank.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "slug", nullable = false, length = 120, unique = true)
    private String slug;
}


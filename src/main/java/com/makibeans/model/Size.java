package com.makibeans.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Entity representing a Size.
 */

@Entity
@NoArgsConstructor
@Getter
@Table(name = "size")
@ToString
public class Size {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Setter
    @NotBlank(message = "Size name should not be blank.")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    public Size(String name) {
        this.name = name;
    }
}

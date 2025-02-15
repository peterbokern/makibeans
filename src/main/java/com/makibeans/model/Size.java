package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

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
    @Column(name = "size_description", nullable = false, length = 100)
    private String sizeDescription;

    public Size(String sizeDescription) {
        this.sizeDescription = sizeDescription;
    }
}

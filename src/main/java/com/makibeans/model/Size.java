package com.makibeans.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Table(name = "size")
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

    @Override
    public String toString() {
        return "Size{" +
                "id=" + id +
                ", size='" + sizeDescription + '\'' +
                '}';
    }
}

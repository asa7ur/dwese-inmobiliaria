package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Entity
@Table(name = "property_images")
@Data
@NoArgsConstructor
public class PropertyImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String fileName;

    @ManyToOne
    @JoinColumn(name = "property_id")
    @ToString.Exclude // CRUCIAL: Evita StackOverflowError al imprimir Property -> Image -> Property
    @EqualsAndHashCode.Exclude
    private Property property;

    public PropertyImage(String fileName, Property property) {
        this.fileName = fileName;
        this.property = property;
    }
}
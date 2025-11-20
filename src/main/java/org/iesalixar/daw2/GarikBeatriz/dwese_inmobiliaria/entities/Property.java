package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "properties")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.property.description.notEmpty}")
    @Size(max = 500, message = "{msg.property.description.size}")
    @Column(name = "description", nullable = false)
    private String description;

    @NotEmpty(message = "{msg.property.location.notEmpty}")
    @Size(max = 100, message = "{msg.property.location.size}")
    @Column(name = "location", nullable = false)
    private String location;

    @NotNull(message = "{msg.property.price.notNull}")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private double price;

    @NotEmpty(message = "{msg.property.type.notEmpty}")
    @Column(name = "type", nullable = false)
    private String type;

    @NotEmpty(message = "{msg.property.floors.notNull}")
    @Column(name = "floors", nullable = false)
    private int floors;

    @NotEmpty(message = "{msg.property.bedrooms.notNull}")
    @Column(name = "bedrooms", nullable = false)
    private int bedrooms;

    @NotEmpty(message = "{msg.property.bathrooms.notNull}")
    @Column(name = "bathrooms", nullable = false)
    private int bathrooms;

    @NotEmpty(message = "{msg.property.status.notEmpty}")
    @Size(max = 100, message = "{msg.property.status.size}")
    @Column(name = "status", nullable = false)
    private String status;
}

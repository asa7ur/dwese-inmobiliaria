package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils.EntityCodeGenerator;

import java.math.BigDecimal;
import java.util.List;

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

    @Transient
    private String code;

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
    private BigDecimal price;

    @NotNull(message = "{msg.property.type.notEmpty}")
    @Enumerated(EnumType.STRING)
    private Type type;

    @Positive(message = "{msg.property.floors.positive}")
    @Column(name = "floors", nullable = false)
    private int floors;

    @Positive(message = "{msg.property.bedrooms.positive}")
    @Column(name = "bedrooms", nullable = false)
    private int bedrooms;

    @Positive(message = "{msg.property.bathrooms.positive}")
    @Column(name = "bathrooms", nullable = false)
    private int bathrooms;

    @NotNull(message = "{msg.property.status.notNull}")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Status status;

    @OneToOne(mappedBy = "property")
    private Transaction transaction;

    @Column(name = "image")
    private String image;

    @ManyToMany
    @JoinTable(
            name = "property_agent",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    private List<Agent> agents;

    public enum Status {
        AVAILABLE, RESERVED, SOLD;
    }

    public enum Type {
        HOUSE, FLAT, CABIN, CASTLE, VILLA;
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void generateCode() {
        this.code = EntityCodeGenerator.generateCode(this.getClass(), this.id);
    }

    public Property (String code, String description, String location, BigDecimal price, Type type, int floors, int bedrooms, int bathrooms, Status status) {
        this.code = code;
        this.description = description;
        this.location = location;
        this.price = price;
        this.type = type;
        this.floors = floors;
        this.bedrooms = bedrooms;
        this.bathrooms = bathrooms;
        this.status = status;
    }
}

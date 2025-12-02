package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils.EntityCodeGenerator;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "properties")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Property {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Si usas @Transient, este campo no se guarda en BD.
    // Si quieres guardarlo, quita @Transient.
    @Transient
    private String code;

    @NotEmpty(message = "{msg.property.name.notEmpty}")
    @Size(max = 100, message = "{msg.property.name.size}")
    @Column(name = "name", nullable = false)
    private String name;

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
    @ToString.Exclude // Evita bucles infinitos
    @EqualsAndHashCode.Exclude
    private Transaction transaction;

    @ManyToMany
    @JoinTable(
            name = "property_agent",
            joinColumns = @JoinColumn(name = "property_id"),
            inverseJoinColumns = @JoinColumn(name = "agent_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Agent> agents;

    public enum Status {
        AVAILABLE, RESERVED, SOLD;
    }

    public enum Type {
        HOUSE, FLAT, CABIN, CASTLE, VILLA;
    }

    @OneToMany(mappedBy = "property", cascade = CascadeType.ALL, orphanRemoval = true)
    @ToString.Exclude // CRUCIAL: Evita StackOverflowError
    @EqualsAndHashCode.Exclude
    private List<PropertyImage> images = new ArrayList<>();

    public void addImage(String filename) {
        PropertyImage image = new PropertyImage(filename, this);
        this.images.add(image);
    }

    // Metodo helper para eliminar imágenes de la lista
    public void removeImage(PropertyImage image) {
        this.images.remove(image);
        image.setProperty(null);
    }

    @PostLoad
    @PostPersist
    @PostUpdate
    private void generateCode() {
        if (this.id != null) {
            this.code = EntityCodeGenerator.generateCode(this.getClass(), this.id);
        }
    }
}
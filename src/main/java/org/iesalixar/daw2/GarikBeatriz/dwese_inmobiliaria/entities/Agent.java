package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils.EntityCodeGenerator;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "agents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Agent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String code;

    @NotEmpty(message = "{msg.agent.dni.notEmpty}")
    @Column(name = "dni", nullable = false, length = 20)
    private String dni;

    @NotEmpty(message = "{msg.agent.name.notEmpty}")
    @Size(max = 100, message = "{msg.agent.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotEmpty(message = "{msg.agent.phone.notEmpty}")
    @Size(max = 25, message = "{msg.agent.phone.size}")
    @Column(name = "phone", nullable = false, length = 25)
    private String phone;

    @NotEmpty(message = "{msg.agent.email.notEmpty}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "{msg.agent.email.notValid}")
    @Size(max = 100, message = "{msg.agent.email.size}")
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @NotNull(message = "{msg.agent.office.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "office_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Office office;

    @Column(name = "image", length = 255)
    private String image;

    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "agent", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Transaction> transactions;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "property_agent",
            joinColumns = @JoinColumn(name = "agent_id"),
            inverseJoinColumns = @JoinColumn(name = "property_id")
    )
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Property> properties = new ArrayList<>();

    @PostLoad
    @PostPersist
    @PostUpdate
    private void generateCode() {
        this.code = EntityCodeGenerator.generateCode(this.getClass(), this.dni);
    }

    public Agent(String code, String name, String dni, String phone, String email, Office office) {
        this.code = code;
        this.name = name;
        this.dni = dni;
        this.phone = phone;
        this.email = email;
        this.office = office;
    }
}
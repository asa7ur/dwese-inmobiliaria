package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils.EntityCodeGenerator;

import java.util.List;

@Entity
@Table(name = "offices")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String code;

    @NotEmpty(message = "{msg.office.name.notEmpty}")
    @Size(max = 100, message = "{msg.office.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotEmpty(message = "{msg.office.address.notEmpty}")
    @Column(name = "address", nullable = false)
    private String address;

    @NotEmpty(message = "{msg.office.phone.notEmpty}")
    @Size(max = 25, message = "{msg.office.phone.size}")
    @Column(name = "phone", nullable = false, length = 25)
    private String phone;

    @NotEmpty(message = "{msg.office.email.notEmpty}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "{msg.client.email.notValid}")
    @Size(max = 100)
    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @OneToMany(mappedBy = "office", fetch = FetchType.LAZY)
    private List<Agent> agents;

    @PostLoad
    @PostPersist
    @PostUpdate
    private void generateCode() {
        this.code = EntityCodeGenerator.generateCode(this.getClass(), this.id);
    }

    public Office (String code, String name, String address, String phone, String email) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
}

package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.loadtime.Agent;

import java.util.List;

@Entity
@Table(name = "offices")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Office {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.office.code.notEmpty}")
    @Size(max = 5, message = "{msg.office.code.size}")
    @Column(name = "code", nullable = false, length = 5)
    private String code;

    @NotEmpty(message = "{msg.office.name.notEmpty}")
    @Size(max = 100, message = "{msg.office.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotEmpty(message = "{msg.office.address.notEmpty}")
    @Column(name = "address", nullable = false)
    private String address;

    @NotEmpty(message = "{msg.office.phone.notEmpty}")
    @Size(max = 10, message = "{msg.office.phone.size}")
    @Column(name = "phone", nullable = false, length = 10)
    private String phone;

    @NotEmpty(message = "{msg.office.email.notEmpty}")
    @Column(name = "email", nullable = false)
    private String email;

    @OneToMany(mappedBy = "office", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Agent> agents;

    public Office (String code, String name, String address, String phone, String email) {
        this.code = code;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.email = email;
    }
}

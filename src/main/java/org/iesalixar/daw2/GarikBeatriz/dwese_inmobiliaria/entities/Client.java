package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "clients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Client {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotEmpty(message = "{msg.client.code.notEmpty}")
    @Size(max = 5, message = "{msg.client.code.size}")
    @Column(name = "code", nullable = false, length = 2)
    private String code;

    @NotEmpty(message = "{msg.client.name.notEmpty}")
    @Size(max = 100, message = "{msg.client.name.size}")
    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @NotEmpty(message = "{msg.client.phone.notEmpty}")
    @Size(max = 25, message = "{msg.client.phone.size}")
    @Column(name = "phone", nullable = false, length = 25)
    private String phone;

    @NotEmpty(message = "{msg.client.name.notEmpty}")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$", message = "{msg.client.email.notValid}")
    @Column(name = "email", nullable = false, length = 25)
    private String email;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Appointment> appointments;

    @OneToMany(mappedBy = "client", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<Transaction> transactions;
    
    public Client(String code, String name, String phone, String email) {
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.email = email;
    }
}

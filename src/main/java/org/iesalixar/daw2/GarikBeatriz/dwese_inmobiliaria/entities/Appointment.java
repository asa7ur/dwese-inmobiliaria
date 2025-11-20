package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import ch.qos.logback.core.net.server.Client;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.aspectj.weaver.loadtime.Agent;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalTime;
import java.util.Date;

@Entity
@Table(name = "appointments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.appointment.code.notEmpty}")
    @Size(max = 5, message = "{msg.appointment.code.size}")
    @Column(name = "code", nullable = false, length = 5)
    private String code;

    @NotNull(message = "{msg.appointment.date.notNull}")
    @Temporal(TemporalType.TIMESTAMP)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "date", nullable = false)
    private Date date;

    @NotNull(message = "{msg.appointment.time.notNull}")
    @Temporal(TemporalType.TIME)
    @DateTimeFormat(pattern = "HH:mm")
    @Column(name = "time", nullable = false)
    private LocalTime time;

    @NotEmpty(message = "{msg.appointment.location.notEmpty}")
    @Size(max = 100, message = "{msg.appointment.location.size}")
    @Column(name = "location", nullable = false)
    private String location;

    @NotEmpty(message = "{msg.appointment.notes.notEmpty}")
    @Column(name = "notes")
    private String notes;

    @NotNull(message = "{msg.appointment.agent.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    private Agent agent;

    @NotNull(message = "{msg.appointment.client.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    private Client client;

    public Appointment(String code, Date date, LocalTime time, String location, String notes, Agent agent, Client client) {
        this.code = code;
        this.date = date;
        this.time = time;
        this.location = location;
        this.notes = notes;
        this.agent = agent;
        this.client = client;
    }
}

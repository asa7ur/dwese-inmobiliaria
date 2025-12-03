package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils.EntityCodeGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.*;

@Entity
@Table(name = "appointments")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Appointment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String code;

    @Transient
    @NotNull(message = "{msg.appointment.timestamp.notNull}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate appointmentDate;

    @Column(name = "appointment_timestamp", nullable = false)
    private long appointmentTimestamp;

    @NotEmpty(message = "{msg.appointment.location.notEmpty}")
    @Size(max = 100, message = "{msg.appointment.location.size}")
    @Column(name = "location", nullable = false)
    private String location;

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

    public LocalDate getAppointmentDate() {
        if (appointmentDate == null) {
            return Instant.ofEpochSecond(appointmentTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDate appointmentDate) {
        this.appointmentDate = appointmentDate;
        this.appointmentTimestamp = appointmentDate
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    @PostLoad
    public void onLoad() {
        this.generateCode();

        if (this.appointmentDate == null && this.appointmentTimestamp > 0) {
            this.appointmentDate = Instant.ofEpochSecond(this.appointmentTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
    }
    @PostPersist
    @PostUpdate
    private void generateCode() {
        this.code = EntityCodeGenerator.generateCode(this.getClass(), this.id);
    }

    public Appointment(String code,
                       long timestamp,
                       String location,
                       String notes,
                       Agent agent,
                       Client client) {
        this.code = code;
        this.appointmentTimestamp = timestamp;
        this.location = location;
        this.notes = notes;
        this.agent = agent;
        this.client = client;
    }
}

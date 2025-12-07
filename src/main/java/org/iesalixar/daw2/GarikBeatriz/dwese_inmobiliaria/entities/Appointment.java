package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
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
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime appointmentDate;

    @Column(name = "appointment_timestamp", nullable = false)
    private long appointmentTimestamp;

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

    @NotNull(message = "{msg.appointment.property.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "property_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Property property;

    public LocalDateTime getAppointmentDate() {
        if (appointmentDate == null && appointmentTimestamp > 0) {
            return Instant.ofEpochSecond(appointmentTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
        return appointmentDate;
    }

    public void setAppointmentDate(LocalDateTime appointmentDate) {
        this.appointmentDate = appointmentDate;
        if (appointmentDate != null) {
            this.appointmentTimestamp = appointmentDate
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
        }
    }

    @PostLoad
    public void onLoad() {
        this.generateCode();
        if (this.appointmentDate == null && this.appointmentTimestamp > 0) {
            this.appointmentDate = Instant.ofEpochSecond(this.appointmentTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();
        }
    }

    @PostPersist
    @PostUpdate
    private void generateCode() {
        this.code = EntityCodeGenerator.generateCode(this.getClass(), this.id);
    }

    public Appointment(String code,
                       long timestamp,
                       String notes,
                       Agent agent,
                       Client client) {
        this.code = code;
        this.appointmentTimestamp = timestamp;
        this.notes = notes;
        this.agent = agent;
        this.client = client;
    }
}
package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotEmpty(message = "{msg.agent.code.notEmpty}")
    @Size(max = 5, message = "{msg.agent.code.size}")
    @Column(name = "code", nullable = false, length = 2)
    private String code;

    @NotNull(message = "{msg.transaction.timestamp.notNull}")
    private long transactionTimestamp;

    @NotNull(message = "{msg.transaction.status.notNull}")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Positive(message = "{msg.transaction.price.positive}")
    private double price;

    public LocalDate getTransactionDate() {
        return Instant.ofEpochSecond(transactionTimestamp)
                .atZone(java.time.ZoneId.systemDefault())
                .toLocalDate();
    }

    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionTimestamp = transactionDate.atStartOfDay()
                .toEpochSecond(java.time.ZoneOffset.UTC);
    }

    public enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    @OneToOne
    @JoinColumn(name ="property_id")
    private Property property;

    @NotNull(message = "{msg.transaction.client.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "client_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Client client;

    @NotNull(message = "{msg.transaction.agent.notNull}")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "agent_id", nullable = false)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Agent agent;

    public Transaction(String code, long transactionTimestamp, Status status, double price, Property property, Client client, Agent agent) {
        this.code = code;
        this.transactionTimestamp = transactionTimestamp;
        this.status = status;
        this.price = price;
        this.property = property;
        this.client = client;
        this.agent = agent;
    }
}

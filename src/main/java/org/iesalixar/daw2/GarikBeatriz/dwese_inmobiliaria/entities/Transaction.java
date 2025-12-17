package org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.entities;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;
import org.iesalixar.daw2.GarikBeatriz.dwese_inmobiliaria.utils.EntityCodeGenerator;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;

@Entity
@Table(name = "transactions")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Transaction {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Transient
    private String code;

    @Transient
    @NotNull(message = "{msg.transaction.timestamp.notNull}")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate transactionDate;

    @Column(name = "transaction_timestamp", nullable = false)
    private long transactionTimestamp;

    @NotNull(message = "{msg.transaction.status.notNull}")
    @Enumerated(EnumType.STRING)
    private Status status;

    @Positive(message = "{msg.transaction.price.positive}")
    @Column(name = "price", nullable = false, precision = 10, scale = 2)
    private BigDecimal price;

    /**
     * GETTER PERSONALIZADO
     * Este metodo se usa cuando pides la fecha (ej: en el HTML con th:text="${transaction.transactionDate}").
     * * Lógica:
     * 1. Si 'transactionDate' (campo LocalDate) ya tiene valor, lo devuelve.
     * 2. Si es null (ej: acabamos de cargar de la BD donde solo se guarda el timestamp),
     * convierte el 'transactionTimestamp' (segundos) a un objeto LocalDate.
     * * Esto permite tratar la fecha como un objeto Java normal aunque en la BD sea un número.
     */
    public LocalDate getTransactionDate() {
        if (transactionDate == null) {
            return Instant.ofEpochSecond(transactionTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
        return transactionDate;
    }

    /**
     * SETTER PERSONALIZADO
     * Este metodo se llama cuando el formulario envía la fecha (ej: al crear/editar).
     * * Lógica:
     * 1. Guarda la fecha en el campo 'transactionDate' (para uso inmediato en Java).
     * 2. IMPORTANTE: Calcula y guarda también el 'transactionTimestamp'.
     * Convierte la fecha al inicio del día (00:00:00) en la zona horaria del sistema
     * y extrae los segundos (Epoch Second).
     * * Así mantenemos sincronizado el campo que se guarda en BD (timestamp) con el que se usa en la App (date).
     */
    public void setTransactionDate(LocalDate transactionDate) {
        this.transactionDate = transactionDate;
        this.transactionTimestamp = transactionDate
                .atStartOfDay(ZoneId.systemDefault())
                .toEpochSecond();
    }

    public enum Status {
        PENDING, COMPLETED, CANCELLED
    }

    @NotNull(message = "{msg.transaction.property.notNull}")
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

    /**
     * LIFECYCLE CALLBACK: @PostLoad
     * Se ejecuta automáticamente justo DESPUÉS de que Hibernate cargue los datos de la BD.
     * * Utilidad:
     * 1. Genera el código visual de la transacción (ej: "TR00123") para que esté listo.
     * 2. Rellena el campo 'transactionDate' (LocalDate) a partir del 'transactionTimestamp'
     * que acaba de traer de la base de datos.
     * Sin esto, 'transactionDate' sería null al leer de la base de datos porque es @Transient.
     */
    @PostLoad
    public void onLoad() {
        this.generateCode();

        if (this.transactionDate == null && this.transactionTimestamp > 0) {
            this.transactionDate = Instant.ofEpochSecond(this.transactionTimestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate();
        }
    }

    /**
     * LIFECYCLE CALLBACK: @PostPersist y @PostUpdate
     * Se ejecuta automáticamente DESPUÉS de hacer un INSERT o un UPDATE en la base de datos.
     * * Utilidad:
     * - Asegura que el código de negocio (ej: "TR12345") se genere o actualice
     * siempre que la entidad se guarde.
     * - EntityCodeGenerator usa el ID (que se genera al persistir) para crear este código.
     */
    @PostPersist
    @PostUpdate
    private void generateCode() {
        this.code = EntityCodeGenerator.generateCode(this.getClass(), this.id);
    }

    public Transaction(String code, long transactionTimestamp, Status status, BigDecimal price, Property property, Client client, Agent agent) {
        this.code = code;
        this.transactionTimestamp = transactionTimestamp;
        this.status = status;
        this.price = price;
        this.property = property;
        this.client = client;
        this.agent = agent;
    }
}

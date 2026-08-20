package dev.muliroz.paymentjava.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@NoArgsConstructor
@Entity
@Table(name = "outbox")
public class OutboxMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String aggregateType;
    private UUID aggregateId;
    private String topic;

    @Column(columnDefinition = "TEXT")
    private String payload;

    private String status;
    private LocalDateTime createdAt;

    @PrePersist
    public void onCreate() {
        this.status = "PENDENTE";
        this.createdAt = LocalDateTime.now();
    }

    public OutboxMessage(String aggregateType, UUID aggregateId, String topic, String payload) {
        this.aggregateType = aggregateType;
        this.aggregateId = aggregateId;
        this.topic = topic;
        this.payload = payload;
    }

    public void markAsProcessed() {
        this.status = "PROCESSADO";
    }
}

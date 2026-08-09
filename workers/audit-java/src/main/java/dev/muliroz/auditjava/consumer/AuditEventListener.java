package dev.muliroz.auditjava.consumer;

import dev.muliroz.auditjava.dto.GenericEventEnvelope;
import dev.muliroz.auditjava.model.AuditLogEntity;
import dev.muliroz.auditjava.repository.AuditLogRepository;
import jakarta.transaction.Transactional;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.BackOff;
import org.springframework.kafka.annotation.DltHandler;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.annotation.RetryableTopic;
import org.springframework.kafka.retrytopic.TopicSuffixingStrategy;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.util.UUID;

@Component
public class AuditEventListener {

    private static final Logger log = LoggerFactory.getLogger(AuditEventListener.class);

    private final AuditLogRepository repository;
    private final ObjectMapper objectMapper;

    public AuditEventListener(AuditLogRepository repository, ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    @RetryableTopic(
        attempts = "3",
        backOff = @BackOff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = "-dlt",
        include = {Exception.class}
    )
    @KafkaListener(
        topics = {
            "orders-created",
            "orders-cancelled",
            "items-created",
            "items-updated",
            "items-deactivated"
        },
        groupId = "audit-worker-group"
    )
    @Transactional
    public void consume(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            GenericEventEnvelope event = objectMapper.readValue(record.value(), GenericEventEnvelope.class);
            UUID eventId = event.metadata().eventId();

            if (repository.existsByEventId(eventId)) {
                log.warn("Evento com ID {} já foi processado. Descartando duplicata.", eventId);
                ack.acknowledge();
                return;
            }

            AuditLogEntity auditLog = new AuditLogEntity();
            auditLog.setEventId(eventId);
            auditLog.setEventType(event.metadata().eventType());
            auditLog.setAggregateType(extractAggregateType(record.topic()));
            auditLog.setAggregateId(UUID.fromString(record.key()));
            auditLog.setPayload(event.payload());
            auditLog.setOccurredAt(event.metadata().occurredAt());

            repository.save(auditLog);
            log.info("Auditoria registrada para evento ID {} do tópico {}", eventId, record.topic());

            ack.acknowledge();

        } catch (Exception e) {
            log.error("Erro ao processar evento de auditoria da partição {} offset {}: {}",
                    record.partition(), record.offset(), e.getMessage(), e);
            throw new RuntimeException("Falha no processamento para acionar o retry do Kafka", e);
        }
    }

    @DltHandler
    public void handlerDlt(
            ConsumerRecord<String, String> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment ack
    ) {
        log.error("Mensagem encaminhada para DLQ [{}] após esgotar retries. Key: {}, Payload: {}",
                topic, record.key(), record.value()
        );
        ack.acknowledge();
    }

    private String extractAggregateType(String topic) {
        if (topic.startsWith("orders-")) return "PEDIDO";
        if (topic.startsWith("items-")) return "ITEM";
        return "DESCONHECIDO";
    }
}

package dev.muliroz.paymentjava.consumer;

import dev.muliroz.paymentjava.dto.InventoryReservedEventDTO;
import dev.muliroz.paymentjava.events.EventEnvelope;
import dev.muliroz.paymentjava.events.payload.OrderCreatedPayload;
import dev.muliroz.paymentjava.service.PaymentService;
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
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

@Component
public class PaymentEventListener {

    public static final Logger log = LoggerFactory.getLogger(PaymentEventListener.class);

    private final PaymentService paymentService;
    private final ObjectMapper objectMapper;

    public PaymentEventListener(PaymentService paymentService, ObjectMapper objectMapper) {
        this.paymentService = paymentService;
        this.objectMapper = objectMapper;
    }

    @RetryableTopic(
        attempts = "3",
        backOff = @BackOff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = "-dlt",
        include = {Exception.class}
    )
    @KafkaListener(topics = "orders-created", groupId = "payment-worker-group")
    public void consumeOrderCreated(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Recebendo evento orders-created. Key: {}, Offset: {}", record.key(), record.offset());
            EventEnvelope<OrderCreatedPayload> event = objectMapper.readValue(
                    record.value(),
                    new TypeReference<>() {}
            );

            paymentService.handleOrderCreated(event.metadata().eventId(), event.payload());
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erro ao processar orders-created [Offset: {}]: {}", record.offset(), e.getMessage(), e);
            throw new RuntimeException("Falha no processamento de orders-created", e);
        }
    }

    @RetryableTopic(
        attempts = "3",
        backOff = @BackOff(delay = 1000, multiplier = 2.0),
        topicSuffixingStrategy = TopicSuffixingStrategy.SUFFIX_WITH_INDEX_VALUE,
        dltTopicSuffix = "-dlt",
        include = {Exception.class}
    )
    @KafkaListener(topics = "inventory-reserved", groupId = "payment-worker-group")
    public void consumeInventoryReserved(ConsumerRecord<String, String> record, Acknowledgment ack) {
        try {
            log.info("Recebendo evento inventory-reserved. Key: {}, Offset: {}", record.key(), record.offset());
            InventoryReservedEventDTO event = objectMapper.readValue(record.value(), InventoryReservedEventDTO.class);

            paymentService.processPayment(event);
            ack.acknowledge();
        } catch (Exception e) {
            log.error("Erro ao processar inventory-reserved [Offset: {}]: {}", record.offset(), e.getMessage(), e);
            throw new RuntimeException("Falha no processamento de inventory-reserved", e);
        }
    }

    @DltHandler
    public void handleDlt(
            ConsumerRecord<String, String> record,
            @Header(KafkaHeaders.RECEIVED_TOPIC) String topic,
            Acknowledgment ack
    ) {
        log.error("Mensagem encaminhada para DLQ [{}] após esgotar retries. Key: {}, Payload: {}",
                topic, record.key(), record.value()
        );
        ack.acknowledge();
    }
}

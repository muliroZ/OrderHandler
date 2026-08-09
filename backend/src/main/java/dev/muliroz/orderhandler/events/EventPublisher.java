package dev.muliroz.orderhandler.events;

import dev.muliroz.orderhandler.model.OutboxMessage;
import dev.muliroz.orderhandler.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class EventPublisher {

    private static final Logger log = LoggerFactory.getLogger(EventPublisher.class);

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final OutboxRepository outboxRepository;

    public EventPublisher(KafkaTemplate<String, String> kafkaTemplate, OutboxRepository outboxRepository) {
        this.kafkaTemplate = kafkaTemplate;
        this.outboxRepository = outboxRepository;
    }

    @Scheduled(fixedDelay = 2000)
    @Transactional
    public void publishPendingEvents() {
        List<OutboxMessage> pendingMessages = outboxRepository.findTop100ByStatusOrderByCreatedAtAsc("PENDENTE");

        for (OutboxMessage message : pendingMessages) {
            try {
                kafkaTemplate.send(
                        message.getTopic(),
                        message.getAggregateId().toString(),
                        message.getPayload()
                ).get();

                message.markAsProcessed();
                outboxRepository.save(message);
            } catch (Exception e) {
                log.error(
                        "Falha ao enviar a mensagem de outbox com ID {} para o tópico {}",
                        message.getId(), message.getTopic(), e
                );
            }
        }
    }
}

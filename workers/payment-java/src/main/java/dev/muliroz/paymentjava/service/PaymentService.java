package dev.muliroz.paymentjava.service;

import dev.muliroz.paymentjava.client.PaymentGatewayClient;
import dev.muliroz.paymentjava.dto.ChargeCommand;
import dev.muliroz.paymentjava.dto.InventoryReservedEventDTO;
import dev.muliroz.paymentjava.dto.PaymentResultDTO;
import dev.muliroz.paymentjava.events.*;
import dev.muliroz.paymentjava.events.payload.OrderCreatedPayload;
import dev.muliroz.paymentjava.events.payload.PaymentApprovedPayload;
import dev.muliroz.paymentjava.events.payload.PaymentRejectedPayload;
import dev.muliroz.paymentjava.model.OutboxMessage;
import dev.muliroz.paymentjava.model.PaymentEntity;
import dev.muliroz.paymentjava.model.PaymentStatus;
import dev.muliroz.paymentjava.repository.OutboxRepository;
import dev.muliroz.paymentjava.repository.PaymentRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.UUID;

@Service
public class PaymentService {

    public static final Logger log = LoggerFactory.getLogger(PaymentService.class);

    private final PaymentGatewayClient paymentGatewayClient;
    private final PaymentRepository paymentRepository;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public PaymentService(
            PaymentGatewayClient paymentGatewayClient,
            PaymentRepository paymentRepository,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.paymentGatewayClient = paymentGatewayClient;
        this.paymentRepository = paymentRepository;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    @Transactional
    public void handleOrderCreated(UUID eventId, OrderCreatedPayload payload) {
        if (paymentRepository.existsByEventId(eventId)) {
            log.warn("Evento de pedido {} já foi recebido anteriormente.", eventId);
            return;
        }

        PaymentEntity payment = new PaymentEntity();
        payment.setEventId(eventId);
        payment.setOrderId(payload.orderId());
        payment.setAmount(payload.subtotal());
        payment.setMethod(payload.paymentMethod());
        payment.setCustomerEmail(payload.customerEmail());
        payment.setCardToken(payload.cardToken());
        payment.setInstallments(payload.installments());
        payment.setStatus(PaymentStatus.AGUARDANDO_ESTOQUE);

        paymentRepository.save(payment);
        log.info("Intenção de pagamento registrada para o pedido {}", payload.orderId());
    }

    @Transactional
    public void processPayment(InventoryReservedEventDTO event) {
        if (paymentRepository.existsByEventId(event.id())) {
            log.warn("Evento de pagamento {} já foi processado anteriormente.", event.id());
            return;
        }

        if (paymentRepository.existsByOrderIdAndStatus(event.orderId(), PaymentStatus.APROVADO)) {
            log.warn("Pedido {} já possui um pagamento aprovado. Ignorando cobrança.", event.orderId());
            return;
        }

        PaymentEntity payment = paymentRepository.findByOrderId(event.orderId())
                .orElseThrow(() -> new RuntimeException("Pedido " + event.orderId() + " ainda não recebido."));

        if (payment.getStatus() != PaymentStatus.AGUARDANDO_ESTOQUE) {
            log.warn("Pedido {} não está no status AGUARDANDO_ESTOQUE (Status atual: {}). Ignorando.",
                    event.orderId(), payment.getStatus());
            return;
        }

        ChargeCommand command = new ChargeCommand(
                payment.getOrderId(),
                event.id(),
                payment.getAmount(),
                payment.getMethod(),
                payment.getCustomerEmail(),
                payment.getCardToken(),
                payment.getInstallments()
        );

        PaymentResultDTO result = paymentGatewayClient.charge(command);

        payment.setStatus(result.status());
        payment.setGatewayPaymentId(result.gatewayPaymentId());
        payment.setFailureReason(result.failureReason());
        payment.setProcessedAt(Instant.now());
        paymentRepository.save(payment);

        if (result.status() == PaymentStatus.APROVADO) {
            handlePaymentApproved(event, payment);
        } else {
            handlePaymentRejected(event, payment, result.failureReason());
        }
    }

    private void handlePaymentApproved(InventoryReservedEventDTO event, PaymentEntity payment) {
        log.info("Pagamento aprovado para o pedido {}.", event.orderId());

        EventEnvelope<PaymentApprovedPayload> outboxEvent = new EventEnvelope<>(
                new EventMetadata(EventType.PAYMENT_APPROVED, "1.0"),
                new PaymentApprovedPayload(
                        payment.getId(),
                        event.orderId(),
                        event.id(),
                        payment.getGatewayPaymentId()
                )
        );

        OutboxMessage outbox = new OutboxMessage(
                "PAGAMENTO",
                event.orderId(),
                "payments-approved",
                serialize(outboxEvent)
        );
        outboxRepository.save(outbox);
    }

    private void handlePaymentRejected(InventoryReservedEventDTO event, PaymentEntity payment, String reason) {
        log.info("Pagamento recusado para o pedido {}. Motivo: {}", event.orderId(), reason);

        EventEnvelope<PaymentRejectedPayload> outboxEvent = new EventEnvelope<>(
                new EventMetadata(EventType.PAYMENT_REJECTED, "1.0"),
                new PaymentRejectedPayload(
                        payment.getId(),
                        event.orderId(),
                        reason,
                        event.items()
                )
        );

        OutboxMessage outbox = new OutboxMessage(
                "PAGAMENTO",
                event.orderId(),
                "payments-rejected",
                serialize(outboxEvent)
        );
        outboxRepository.save(outbox);
    }

    private String serialize(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (JacksonException e) {
            throw new IllegalStateException("Erro ao serializar evento de pagamento: " + e.getMessage(), e);
        }
    }
}

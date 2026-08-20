package dev.muliroz.orderhandler.events.payloads;

import dev.muliroz.orderhandler.domain.enums.PaymentMethod;
import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedPayload(
        UUID orderId,
        UUID clientId,
        String customerEmail,
        BigDecimal subtotal,
        PaymentMethod paymentMethod,
        String cardToken,
        Integer installments,
        List<OrderItemDTO> items
) {
}

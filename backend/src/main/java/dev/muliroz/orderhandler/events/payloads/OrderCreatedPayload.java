package dev.muliroz.orderhandler.events.payloads;

import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record OrderCreatedPayload(
        UUID orderId,
        UUID clientId,
        BigDecimal subtotal,
        List<OrderItemDTO> items
) {
}

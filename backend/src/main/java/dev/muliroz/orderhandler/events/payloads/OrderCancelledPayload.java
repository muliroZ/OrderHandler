package dev.muliroz.orderhandler.events.payloads;

import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;

import java.util.List;
import java.util.UUID;

public record OrderCancelledPayload(
        UUID orderId,
        List<OrderItemDTO> items
) {
}

package dev.muliroz.orderhandler.events.payloads;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderCreatedPayload(
        UUID orderId,
        UUID clientId,
        BigDecimal subtotal
) {
}

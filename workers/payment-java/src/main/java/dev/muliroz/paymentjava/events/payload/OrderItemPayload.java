package dev.muliroz.paymentjava.events.payload;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemPayload(
        UUID itemId,
        int quantity,
        BigDecimal unitPrice
) {
}

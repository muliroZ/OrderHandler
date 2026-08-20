package dev.muliroz.orderhandler.dto.internal;

import java.math.BigDecimal;
import java.util.UUID;

public record OrderItemDTO(
        UUID itemId,
        int quantity,
        BigDecimal unitPrice
) {
}

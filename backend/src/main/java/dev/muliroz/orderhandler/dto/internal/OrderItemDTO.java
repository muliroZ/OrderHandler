package dev.muliroz.orderhandler.dto.internal;

import java.util.UUID;

public record OrderItemDTO(
        UUID itemId,
        Integer quantity
) {
}

package dev.muliroz.orderhandler.events.payloads;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemCreatedPayload(
        UUID itemId,
        String name,
        BigDecimal price,
        ItemCategory category,
        int initialQuantity
) {
}

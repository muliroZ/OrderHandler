package dev.muliroz.orderhandler.dto.external;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;

import java.math.BigDecimal;

public record CreateItemRequest(
        String name,
        String description,
        BigDecimal price,
        ItemCategory category,
        int stock
) {
}

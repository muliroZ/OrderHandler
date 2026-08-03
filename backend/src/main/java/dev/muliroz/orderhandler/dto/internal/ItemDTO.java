package dev.muliroz.orderhandler.dto.internal;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;

import java.math.BigDecimal;

public record ItemDTO(
        String name,
        String description,
        BigDecimal price,
        ItemCategory category,
        int stock
) {
}

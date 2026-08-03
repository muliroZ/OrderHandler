package dev.muliroz.orderhandler.dto.external;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import java.math.BigDecimal;

public record UpdateItemRequest(
        String name,
        String description,
        @Positive BigDecimal price,
        ItemCategory category,
        @PositiveOrZero Integer stock
) {
}

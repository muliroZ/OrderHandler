package dev.muliroz.orderhandler.dto.external;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;

import java.math.BigDecimal;

public record ListItemsRequest(
        String term,
        BigDecimal minPrice,
        BigDecimal maxPrice,
        ItemCategory category,
        boolean byStock,
        int page,
        int size,
        String[] ordinations
) {
}

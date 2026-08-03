package dev.muliroz.orderhandler.dto.internal;

import dev.muliroz.orderhandler.domain.enums.OrderStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrderDTO(
        List<OrderItemDTO> orderItems,
        BigDecimal subtotal,
        OrderStatus status,
        LocalDateTime createdAt
) {
}

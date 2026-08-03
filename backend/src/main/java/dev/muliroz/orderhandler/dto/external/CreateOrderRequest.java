package dev.muliroz.orderhandler.dto.external;

import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;
import java.util.UUID;

public record CreateOrderRequest(
        @NotNull UUID clientId,
        @NotEmpty List<OrderItemDTO> orderItems
) {
}

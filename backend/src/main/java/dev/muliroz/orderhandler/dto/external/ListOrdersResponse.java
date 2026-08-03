package dev.muliroz.orderhandler.dto.external;

import dev.muliroz.orderhandler.dto.internal.OrderDTO;

import java.util.List;

public record ListOrdersResponse(
        List<OrderDTO> orders
) {
}

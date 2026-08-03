package dev.muliroz.orderhandler.dto.external;

import java.time.LocalDateTime;

public record ListOrdersRequest(
        LocalDateTime startDate,
        LocalDateTime endDate,
        int page,
        int size,
        boolean sortBySubtotalDesc
) {
}

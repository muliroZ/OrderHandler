package dev.muliroz.paymentjava.dto;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record InventoryReservedEventDTO(
        UUID id,
        UUID originEventId,
        UUID orderId,
        Instant occurredAt,
        List<ReservedItemDTO> items
) {
}

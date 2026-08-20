package dev.muliroz.paymentjava.events.payload;

import dev.muliroz.paymentjava.dto.ReservedItemDTO;

import java.util.List;
import java.util.UUID;

public record PaymentRejectedPayload(
        UUID paymentId,
        UUID orderId,
        String reason,
        List<ReservedItemDTO> items
) {
}

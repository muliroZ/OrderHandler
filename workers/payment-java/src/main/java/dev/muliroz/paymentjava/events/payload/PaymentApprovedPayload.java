package dev.muliroz.paymentjava.events.payload;

import java.util.UUID;

public record PaymentApprovedPayload(
        UUID paymentId,
        UUID orderId,
        UUID originEventId,
        Long gatewayPaymentId
) {
}

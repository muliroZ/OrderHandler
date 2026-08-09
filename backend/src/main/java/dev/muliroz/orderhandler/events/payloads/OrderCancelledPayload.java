package dev.muliroz.orderhandler.events.payloads;

import java.util.UUID;

public record OrderCancelledPayload(
        UUID orderId
) {
}

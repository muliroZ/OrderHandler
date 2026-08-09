package dev.muliroz.orderhandler.events.payloads;

import java.util.UUID;

public record ItemDeletedPayload(
        UUID itemId,
        String name
) {
}

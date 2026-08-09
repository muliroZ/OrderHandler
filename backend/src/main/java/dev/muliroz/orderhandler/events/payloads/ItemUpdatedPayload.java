package dev.muliroz.orderhandler.events.payloads;

import java.util.Map;
import java.util.UUID;

public record ItemUpdatedPayload(
        UUID itemId,
        Map<String, Object> newFields
) {
}

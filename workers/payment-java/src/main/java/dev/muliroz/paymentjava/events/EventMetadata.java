package dev.muliroz.paymentjava.events;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        EventType eventType,
        Instant occurredAt,
        String version
) {
    public EventMetadata(
            EventType eventType,
            String version
    ) {
        this(UUID.randomUUID(), eventType, Instant.now(), version);
    }
}

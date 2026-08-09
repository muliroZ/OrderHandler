package dev.muliroz.auditjava.dto;

import java.time.Instant;
import java.util.UUID;

public record EventMetadata(
        UUID eventId,
        String eventType,
        Instant occurredAt,
        String version
) {}

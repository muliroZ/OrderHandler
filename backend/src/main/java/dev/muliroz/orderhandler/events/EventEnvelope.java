package dev.muliroz.orderhandler.events;

public record EventEnvelope<T>(
        EventMetadata metadata,
        T payload
) {
}

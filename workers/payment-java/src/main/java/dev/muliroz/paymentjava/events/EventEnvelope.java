package dev.muliroz.paymentjava.events;

public record EventEnvelope<T>(
        EventMetadata metadata,
        T payload
) {
}

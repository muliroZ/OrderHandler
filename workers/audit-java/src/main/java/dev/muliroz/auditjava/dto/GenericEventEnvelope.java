package dev.muliroz.auditjava.dto;

import tools.jackson.databind.JsonNode;

public record GenericEventEnvelope(
        EventMetadata metadata,
        JsonNode payload
) {}

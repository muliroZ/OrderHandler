package dev.muliroz.paymentjava.dto;

import java.util.UUID;

public record ReservedItemDTO(
        UUID itemId,
        int quantity
) {
}

package dev.muliroz.paymentjava.dto;

import dev.muliroz.paymentjava.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

public record ChargeCommand(
        UUID orderId,
        UUID eventId,
        BigDecimal amount,
        PaymentMethod method,
        String email,
        String cardToken,
        Integer installments
) {
}

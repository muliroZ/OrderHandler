package dev.muliroz.paymentjava.events.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import dev.muliroz.paymentjava.model.PaymentMethod;

import java.math.BigDecimal;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public record OrderCreatedPayload(
        UUID orderId,
        String customerEmail,
        BigDecimal subtotal,
        PaymentMethod paymentMethod,
        String cardToken,
        Integer installments
) {
}

package dev.muliroz.paymentjava.dto;

import dev.muliroz.paymentjava.model.PaymentStatus;

public record PaymentResultDTO(
        PaymentStatus status,
        Long gatewayPaymentId,
        String failureReason
) {
    public static PaymentResultDTO approved(Long gatewayPaymentId) {
        return new PaymentResultDTO(PaymentStatus.APROVADO, gatewayPaymentId, null);
    }

    public static PaymentResultDTO rejected(Long gatewayPaymentId, String reason) {
        return new PaymentResultDTO(PaymentStatus.RECUSADO, gatewayPaymentId, reason);
    }
}

package dev.muliroz.paymentjava.model;

public enum PaymentMethod {
    PIX("pix"),
    BOLETO("bolbradesco"),
    CREDIT_CARD(null);

    private final String gatewayCode;

    PaymentMethod(String gatewayCode) {
        this.gatewayCode = gatewayCode;
    }

    public String getGatewayCode() {
        return gatewayCode;
    }

    public boolean isCreditCard() {
        return this == CREDIT_CARD;
    }

    public boolean isPix() {
        return this == PIX;
    }
}

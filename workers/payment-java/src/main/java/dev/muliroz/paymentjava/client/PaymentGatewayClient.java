package dev.muliroz.paymentjava.client;

import com.mercadopago.client.payment.PaymentClient;
import com.mercadopago.client.payment.PaymentCreateRequest;
import com.mercadopago.client.payment.PaymentPayerRequest;
import com.mercadopago.core.MPRequestOptions;
import com.mercadopago.exceptions.MPApiException;
import com.mercadopago.exceptions.MPException;
import com.mercadopago.resources.payment.Payment;
import dev.muliroz.paymentjava.dto.ChargeCommand;
import dev.muliroz.paymentjava.dto.PaymentResultDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class PaymentGatewayClient {

    public static final Logger log = LoggerFactory.getLogger(PaymentGatewayClient.class);

    private final PaymentClient paymentClient;

    public PaymentGatewayClient(PaymentClient paymentClient) {
        this.paymentClient = paymentClient;
    }

    public PaymentResultDTO charge(ChargeCommand command) {
        try {
            MPRequestOptions requestOptions = MPRequestOptions.builder()
                    .customHeaders(Map.of("X-Idempotency-Key", command.eventId().toString()))
                    .build();

            PaymentCreateRequest.PaymentCreateRequestBuilder requestBuilder = PaymentCreateRequest.builder()
                    .transactionAmount(command.amount())
                    .description("Cobrança Pedido: " + command.orderId())
                    .payer(PaymentPayerRequest.builder()
                            .email(command.email())
                            .build());

            if (command.method().isPix()) {
                requestBuilder.paymentMethodId(command.method().getGatewayCode());
            } else if (command.method().isCreditCard()){
                requestBuilder
                        .token(command.cardToken())
                        .installments(command.installments() != null ? command.installments() : 1);
            } else {
                requestBuilder.paymentMethodId(command.method().getGatewayCode());
            }

            Payment payment = paymentClient.create(requestBuilder.build(), requestOptions);

            if ("approved".equalsIgnoreCase(payment.getStatus())) {
                return PaymentResultDTO.approved(payment.getId());
            }

            String details = payment.getStatusDetail() != null ? payment.getStatusDetail() : "Pagamento recusado";
            return PaymentResultDTO.rejected(payment.getId(), details);

        } catch (MPApiException exception) {
            int statusCode = exception.getStatusCode();
            log.error("Erro na API do Mercado Pago. Status HTTP: {}, Resposta: {}",
                    statusCode, exception.getApiResponse().getContent()
            );

            if (statusCode >= 400 && statusCode < 500 && statusCode != 429) {
                return PaymentResultDTO.rejected(null, "Dados de pagamento inválidos: " + exception.getMessage());
            }

            throw new RuntimeException("Falha de comunicação transitória com Mercado Pago", exception);

        } catch (MPException exception) {
            log.error("Falha de conexão com o Mercado Pago: {}", exception.getMessage());
            throw new RuntimeException("Timeout ou falha de rede com gateway", exception);
        }
    }
}

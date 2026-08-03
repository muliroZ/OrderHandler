package dev.muliroz.orderhandler.domain.enums;

import java.util.Map;
import java.util.Set;

public enum OrderStatus {
    PENDENTE,
    APROVADO,
    EM_PROCESSAMENTO,
    ENVIADO,
    ENTREGUE,
    CANCELADO;

    public OrderStatus changeStatus(OrderStatus status) {
        Map<OrderStatus, Set<OrderStatus>> changeMap = Map.of(
                PENDENTE,           Set.of(APROVADO, CANCELADO),
                APROVADO,           Set.of(EM_PROCESSAMENTO, CANCELADO),
                EM_PROCESSAMENTO,   Set.of(ENVIADO, CANCELADO),
                ENVIADO,            Set.of(ENTREGUE),
                ENTREGUE,           Set.of(),
                CANCELADO,          Set.of()
        );

        if (!changeMap.get(this).contains(status)) {
            throw new IllegalStateException("Não é possível mudar o status do pedido");
        }

        return status;
    }
}

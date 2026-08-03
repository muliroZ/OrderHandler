package dev.muliroz.orderhandler.domain.actions.order;

import dev.muliroz.orderhandler.domain.contracts.OrderRepository;
import dev.muliroz.orderhandler.domain.entities.Order;
import dev.muliroz.orderhandler.domain.enums.OrderStatus;
import dev.muliroz.orderhandler.domain.exceptions.ResourceNotExistsException;

import java.util.UUID;

public class CancelOrder {

    private final OrderRepository orderRepository;

    public CancelOrder(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public void execute(UUID orderId) {
        Order actualOrder = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotExistsException("O pedido não existe"));

        actualOrder.changeStatus(OrderStatus.CANCELADO);
        orderRepository.save(actualOrder);
    }
}

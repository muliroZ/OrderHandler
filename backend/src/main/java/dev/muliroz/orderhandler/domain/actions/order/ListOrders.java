package dev.muliroz.orderhandler.domain.actions.order;

import dev.muliroz.orderhandler.domain.contracts.OrderRepository;
import dev.muliroz.orderhandler.domain.entities.Order;

import java.util.List;
import java.util.Map;

public class ListOrders {

    private final OrderRepository orderRepository;

    public ListOrders(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public List<Order> execute(Map<String, Object> filters) {
        return orderRepository.search(filters);
    }
}

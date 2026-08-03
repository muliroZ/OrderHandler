package dev.muliroz.orderhandler.domain.contracts;

import dev.muliroz.orderhandler.domain.entities.Order;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface OrderRepository {
    void save(Order order);
    List<Order> search(Map<String, Object> filter);
    Optional<Order> findById(UUID orderId);
}

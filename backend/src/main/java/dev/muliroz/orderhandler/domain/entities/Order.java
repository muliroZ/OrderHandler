package dev.muliroz.orderhandler.domain.entities;

import dev.muliroz.orderhandler.domain.enums.OrderStatus;
import dev.muliroz.orderhandler.domain.exceptions.EmptyOrderException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {

    private final UUID id;
    private final UUID clientId;
    private final List<OrderItem> orderItems;
    private OrderStatus status;
    private final LocalDateTime createdAt;

    public void changeStatus(OrderStatus newStatus) {
        this.status = status.changeStatus(newStatus);
    }

    private Order(UUID clientId, List<OrderItem> orderItems) {
        if (clientId.toString().isBlank()) {
            throw new IllegalArgumentException();
        }

        if (orderItems.isEmpty()) {
            throw new EmptyOrderException("Pedido vazio");
        }

        this.id = UUID.randomUUID();
        this.clientId = clientId;
        this.orderItems = orderItems;
        this.status = OrderStatus.PENDENTE;
        this.createdAt = LocalDateTime.now();
    }

    private Order(
            UUID id,
            UUID clientId,
            List<OrderItem> orderItems,
            OrderStatus status,
            LocalDateTime createdAt
    ) {
        this.id = id;
        this.clientId = clientId;
        this.orderItems = orderItems;
        this.status = status;
        this.createdAt = createdAt;
    }

    public static Order create(UUID clientId, List<OrderItem> orderItems) {
        return new Order(clientId, orderItems);
    }

    public static Order restore(
            UUID id,
            UUID clientId,
            List<OrderItem> orderItems,
            OrderStatus status,
            LocalDateTime createdAt
    ) {
        return new Order(id, clientId, orderItems, status, createdAt);
    }

    public BigDecimal subtotal() {
        return orderItems.stream()
                .map(OrderItem::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public UUID getId() {
        return id;
    }

    public UUID getClientId() {
        return clientId;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
}

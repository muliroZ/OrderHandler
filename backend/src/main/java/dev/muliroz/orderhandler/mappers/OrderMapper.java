package dev.muliroz.orderhandler.mappers;

import dev.muliroz.orderhandler.domain.entities.Order;
import dev.muliroz.orderhandler.dto.internal.OrderDTO;
import dev.muliroz.orderhandler.model.OrderEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderMapper {

    private final OrderItemMapper mapper;

    public OrderMapper(OrderItemMapper mapper) {
        this.mapper = mapper;
    }

    public OrderEntity toEntity(Order order) {
        OrderEntity entity = new OrderEntity();

        entity.setId(order.getId());
        entity.setClientId(order.getClientId());
        entity.setSubtotal(order.subtotal());
        entity.setStatus(order.getStatus());
        entity.setCreatedAt(order.getCreatedAt());

        return entity;
    }

    public Order toDomain(OrderEntity entity) {
        return Order.restore(
                entity.getId(),
                entity.getClientId(),
                entity.getOrderItems().stream()
                        .map(mapper::toDomain)
                        .toList(),
                entity.getStatus(),
                entity.getCreatedAt()
        );
    }

    public OrderDTO toDTO(Order order) {
        return new OrderDTO(
                order.getOrderItems().stream()
                        .map(mapper::toDTO)
                        .toList(),
                order.subtotal(),
                order.getStatus(),
                order.getCreatedAt()
        );
    }
}

package dev.muliroz.orderhandler.mappers;

import dev.muliroz.orderhandler.domain.entities.OrderItem;
import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;
import dev.muliroz.orderhandler.model.ItemEntity;
import dev.muliroz.orderhandler.model.OrderEntity;
import dev.muliroz.orderhandler.model.OrderItemEntity;
import org.springframework.stereotype.Component;

@Component
public class OrderItemMapper {

    private final ItemMapper mapper;

    public OrderItemMapper(ItemMapper mapper) {
        this.mapper = mapper;
    }

    public OrderItemEntity toEntity(OrderItem orderItem, OrderEntity orderEntity, ItemEntity itemEntity) {
        return new OrderItemEntity(
                orderItem.id(),
                orderEntity,
                itemEntity,
                orderItem.quantity(),
                orderItem.unitPrice()
        );
    }

    public OrderItem toDomain(OrderItemEntity entity) {
        return OrderItem.restore(
                entity.getId(),
                mapper.toDomain(entity.getItem()),
                entity.getQuantity(),
                entity.getUnitPrice()
        );
    }

    public OrderItemDTO toDTO(OrderItem orderItem) {
        return new OrderItemDTO(
                orderItem.item().getId(),
                orderItem.quantity(),
                orderItem.unitPrice()
        );
    }
}

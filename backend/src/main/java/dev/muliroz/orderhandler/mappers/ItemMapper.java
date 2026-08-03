package dev.muliroz.orderhandler.mappers;

import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.dto.internal.ItemDTO;
import dev.muliroz.orderhandler.model.ItemEntity;
import org.springframework.stereotype.Component;

@Component
public class ItemMapper {

    public ItemEntity toEntity(Item item) {
        return new ItemEntity(
                item.getId(),
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.getStock(),
                item.isActive()
        );
    }

    public Item toDomain(ItemEntity entity) {
        return Item.restore(
                entity.getId(),
                entity.getName(),
                entity.getDescription(),
                entity.getPrice(),
                entity.getCategory(),
                entity.getStock(),
                entity.isActive()
        );
    }

    public ItemDTO toDTO(Item item) {
        return new ItemDTO(
                item.getName(),
                item.getDescription(),
                item.getPrice(),
                item.getCategory(),
                item.getStock()
        );
    }
}

package dev.muliroz.orderhandler.domain.actions.item;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.domain.enums.ItemCategory;
import dev.muliroz.orderhandler.domain.exceptions.InvalidUpdateException;
import dev.muliroz.orderhandler.domain.exceptions.ResourceNotExistsException;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public class UpdateItem {

    private final ItemRepository itemRepository;

    public UpdateItem(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void execute(UUID itemId, Map<String, Object> fields) {
        if (fields.isEmpty())
            throw new InvalidUpdateException("Nenhum campo foi informado");

        Item oldItem = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotExistsException("O item não existe"));

        Item newItem = Item.restore(
                oldItem.getId(),
                (String) fields.getOrDefault("name", oldItem.getName()),
                (String) fields.getOrDefault("description", oldItem.getDescription()),
                (BigDecimal) fields.getOrDefault("price", oldItem.getPrice()),
                (ItemCategory) fields.getOrDefault("category", oldItem.getCategory()),
                (Integer) fields.getOrDefault("stock", oldItem.getStock()),
                oldItem.isActive()
        );

        itemRepository.save(newItem);
    }
}

package dev.muliroz.orderhandler.domain.actions.item;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.domain.enums.ItemCategory;

import java.math.BigDecimal;

public class CreateItem {

    private final ItemRepository itemRepository;

    public CreateItem(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public void execute(String name, String description, BigDecimal price, ItemCategory category, int stock) {
        Item item = Item.create(
                name,
                description,
                price,
                category,
                stock
        );

        itemRepository.save(item);
    }
}

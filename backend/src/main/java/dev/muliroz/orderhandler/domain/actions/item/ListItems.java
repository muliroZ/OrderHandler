package dev.muliroz.orderhandler.domain.actions.item;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.entities.Item;

import java.util.List;
import java.util.Map;

public class ListItems {

    private final ItemRepository itemRepository;

    public ListItems(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public List<Item> execute(Map<String, Object> filter) {
        return itemRepository.search(filter);
    }
}

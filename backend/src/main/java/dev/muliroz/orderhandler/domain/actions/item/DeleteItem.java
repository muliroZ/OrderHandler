package dev.muliroz.orderhandler.domain.actions.item;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.domain.exceptions.InvalidDeleteException;
import dev.muliroz.orderhandler.domain.exceptions.ResourceNotExistsException;

import java.util.UUID;

public class DeleteItem {

    private final ItemRepository itemRepository;

    public DeleteItem(ItemRepository itemRepository) {
        this.itemRepository = itemRepository;
    }

    public Item execute(UUID itemId) {
        Item target = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotExistsException("O item não existe"));

        if (itemRepository.isUsed(itemId)) {
            throw new InvalidDeleteException("O item já foi usado em pedidos, não é possível excluir");
        }

        Item deletedItem = Item.restore(
                target.getId(),
                target.getName(),
                target.getDescription(),
                target.getPrice(),
                target.getCategory(),
                target.getStock(),
                false
        );

        itemRepository.save(deletedItem);
        return deletedItem;
    }
}

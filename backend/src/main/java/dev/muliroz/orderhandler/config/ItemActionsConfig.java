package dev.muliroz.orderhandler.config;

import dev.muliroz.orderhandler.domain.actions.item.CreateItem;
import dev.muliroz.orderhandler.domain.actions.item.DeleteItem;
import dev.muliroz.orderhandler.domain.actions.item.ListItems;
import dev.muliroz.orderhandler.domain.actions.item.UpdateItem;
import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ItemActionsConfig {

    @Bean
    CreateItem createItem(ItemRepository itemRepository) {
        return new CreateItem(itemRepository);
    }

    @Bean
    DeleteItem deleteItem(ItemRepository itemRepository) {
        return new DeleteItem(itemRepository);
    }

    @Bean
    UpdateItem updateItem(ItemRepository itemRepository) {
        return new UpdateItem(itemRepository);
    }

    @Bean
    ListItems listItems(ItemRepository itemRepository) {
        return new ListItems(itemRepository);
    }
}

package dev.muliroz.orderhandler.service;

import dev.muliroz.orderhandler.domain.actions.item.CreateItem;
import dev.muliroz.orderhandler.domain.actions.item.DeleteItem;
import dev.muliroz.orderhandler.domain.actions.item.ListItems;
import dev.muliroz.orderhandler.domain.actions.item.UpdateItem;
import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.dto.external.CreateItemRequest;
import dev.muliroz.orderhandler.dto.external.ListItemsRequest;
import dev.muliroz.orderhandler.dto.external.ListItemsResponse;
import dev.muliroz.orderhandler.dto.external.UpdateItemRequest;
import dev.muliroz.orderhandler.mappers.ItemMapper;
import jakarta.transaction.Transactional;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class ItemService {

    private final CreateItem createItem;
    private final ListItems listItems;
    private final UpdateItem updateItem;
    private final DeleteItem deleteItem;
    private final ItemMapper mapper;
    private final KafkaTemplate<String, String> kafkaTemplate;

    public ItemService(
            CreateItem createItem,
            ListItems listItems,
            UpdateItem updateItem,
            DeleteItem deleteItem,
            ItemMapper mapper,
            KafkaTemplate<String, String> kafkaTemplate
    ) {
        this.createItem = createItem;
        this.listItems = listItems;
        this.updateItem = updateItem;
        this.deleteItem = deleteItem;
        this.mapper = mapper;
        this.kafkaTemplate = kafkaTemplate;
    }

    public ListItemsResponse search(ListItemsRequest request) {
        Map<String, Object> filter = new HashMap<>();

        filter.put("term", request.term());
        filter.put("minPrice", request.minPrice());
        filter.put("maxPrice", request.maxPrice());
        filter.put("category", request.category());
        filter.put("byStock", request.byStock());
        filter.put("page", request.page());
        filter.put("size", request.size());
        filter.put("ordinations", request.ordinations());

        List<Item> items = listItems.execute(filter);
        return new ListItemsResponse(items.stream().map(mapper::toDTO).toList());
    }

    @Transactional
    public void create(CreateItemRequest request) {
        createItem.execute(
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.stock()
        );
    }

    public void update(UUID itemId, UpdateItemRequest request) {
        Map<String, Object> fields = new HashMap<>();

        if (request.name() != null) {
            fields.put("name", request.name());
        }

        if (request.description() != null) {
            fields.put("description", request.description());
        }

        if (request.price() != null) {
            fields.put("price", request.price());
        }

        if (request.category() != null) {
            fields.put("category", request.category());
        }

        if (request.stock() != null) {
            fields.put("stock", request.stock());
        }

        updateItem.execute(itemId, fields);
    }

    public void delete(UUID itemId) {
        deleteItem.execute(itemId);
    }
}

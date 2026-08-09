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
import dev.muliroz.orderhandler.events.EventEnvelope;
import dev.muliroz.orderhandler.events.EventMetadata;
import dev.muliroz.orderhandler.events.EventType;
import dev.muliroz.orderhandler.events.payloads.ItemCreatedPayload;
import dev.muliroz.orderhandler.events.payloads.ItemDeletedPayload;
import dev.muliroz.orderhandler.events.payloads.ItemUpdatedPayload;
import dev.muliroz.orderhandler.mappers.ItemMapper;
import dev.muliroz.orderhandler.model.OutboxMessage;
import dev.muliroz.orderhandler.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.util.*;

@Service
public class ItemService {

    private final CreateItem createItem;
    private final ListItems listItems;
    private final UpdateItem updateItem;
    private final DeleteItem deleteItem;
    private final ItemMapper mapper;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public ItemService(
            CreateItem createItem,
            ListItems listItems,
            UpdateItem updateItem,
            DeleteItem deleteItem,
            ItemMapper mapper,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.createItem = createItem;
        this.listItems = listItems;
        this.updateItem = updateItem;
        this.deleteItem = deleteItem;
        this.mapper = mapper;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
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
        Item item = createItem.execute(
                request.name(),
                request.description(),
                request.price(),
                request.category(),
                request.stock()
        );

        EventEnvelope<ItemCreatedPayload> event = new EventEnvelope<>(
                new EventMetadata(EventType.ITEM_CREATED, "1.0"),
                new ItemCreatedPayload(item.getId(), item.getName(), item.getPrice(), item.getCategory(), item.getStock())
        );
        String payloadJson = serializeEvent(event);

        OutboxMessage outbox = new OutboxMessage(
                "ITEM",
                item.getId(),
                "items-created",
                payloadJson
        );
        outboxRepository.save(outbox);
    }

    @Transactional
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

        EventEnvelope<ItemUpdatedPayload> event = new EventEnvelope<>(
                new EventMetadata(EventType.ITEM_UPDATED, "1.0"),
                new ItemUpdatedPayload(itemId, fields)
        );
        String payloadJson = serializeEvent(event);

        OutboxMessage outbox = new OutboxMessage(
                "ITEM",
                itemId,
                "items-updated",
                payloadJson
        );
        outboxRepository.save(outbox);
    }

    @Transactional
    public void delete(UUID itemId) {
        Item deletedItem = deleteItem.execute(itemId);

        EventEnvelope<ItemDeletedPayload> event = new EventEnvelope<>(
                new EventMetadata(EventType.ITEM_DEACTIVATED, "1.0"),
                new ItemDeletedPayload(itemId, deletedItem.getName())
        );
        String payloadJson = serializeEvent(event);

        OutboxMessage outbox = new OutboxMessage(
                "ITEM",
                itemId,
                "items-deactivated",
                payloadJson
        );
        outboxRepository.save(outbox);
    }

    private String serializeEvent(Object event) {
        try {
            return objectMapper.writeValueAsString(event);
        } catch (JacksonException e) {
            throw new IllegalStateException("Erro ao serializar evento: " + e.getMessage());
        }
    }
}

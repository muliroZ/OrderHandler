package dev.muliroz.orderhandler.service;

import dev.muliroz.orderhandler.domain.actions.order.CancelOrder;
import dev.muliroz.orderhandler.domain.actions.order.CreateOrder;
import dev.muliroz.orderhandler.domain.actions.order.ListOrders;
import dev.muliroz.orderhandler.domain.entities.Order;
import dev.muliroz.orderhandler.dto.external.CreateOrderRequest;
import dev.muliroz.orderhandler.dto.external.ListOrdersRequest;
import dev.muliroz.orderhandler.dto.external.ListOrdersResponse;
import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;
import dev.muliroz.orderhandler.events.EventEnvelope;
import dev.muliroz.orderhandler.events.EventMetadata;
import dev.muliroz.orderhandler.events.EventType;
import dev.muliroz.orderhandler.events.payloads.OrderCancelledPayload;
import dev.muliroz.orderhandler.events.payloads.OrderCreatedPayload;
import dev.muliroz.orderhandler.mappers.OrderItemMapper;
import dev.muliroz.orderhandler.mappers.OrderMapper;
import dev.muliroz.orderhandler.model.OutboxMessage;
import dev.muliroz.orderhandler.repository.OutboxRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final CreateOrder createOrder;
    private final ListOrders listOrders;
    private final CancelOrder cancelOrder;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OutboxRepository outboxRepository;
    private final ObjectMapper objectMapper;

    public OrderService(
            CreateOrder createOrder,
            ListOrders listOrders,
            CancelOrder cancelOrder,
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper,
            OutboxRepository outboxRepository,
            ObjectMapper objectMapper
    ) {
        this.createOrder = createOrder;
        this.listOrders = listOrders;
        this.cancelOrder = cancelOrder;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.outboxRepository = outboxRepository;
        this.objectMapper = objectMapper;
    }

    public ListOrdersResponse search(ListOrdersRequest request) {
        Map<String, Object> filter = new HashMap<>();

        filter.put("startDate", request.startDate());
        filter.put("endDate", request.endDate());
        filter.put("page", request.page());
        filter.put("size", request.size());
        filter.put("sortBySubtotalDesc", request.sortBySubtotalDesc());

        List<Order> orders = listOrders.execute(filter);
        return new ListOrdersResponse(orders.stream().map(orderMapper::toDTO).toList());
    }

    @Transactional
    public void create(CreateOrderRequest request) {
        Order order = createOrder.execute(request.clientId(), request.orderItems());

        EventEnvelope<OrderCreatedPayload> event = new EventEnvelope<>(
                new EventMetadata(EventType.ORDER_CREATED, "1.0"),
                new OrderCreatedPayload(
                        order.getId(),
                        order.getClientId(),
                        order.subtotal(),
                        request.orderItems()
                )
        );
        String payloadJson = serializeEvent(event);

        OutboxMessage outbox = new OutboxMessage(
                "PEDIDO",
                order.getId(),
                "orders-created",
                payloadJson
        );
        outboxRepository.save(outbox);
    }

    @Transactional
    public void cancel(UUID orderId) {
        Order cancelledOrder = cancelOrder.execute(orderId);

        List<OrderItemDTO> itemsDTO = cancelledOrder.getOrderItems().stream()
                .map(orderItemMapper::toDTO)
                .toList();

        EventEnvelope<OrderCancelledPayload> event = new EventEnvelope<>(
                new EventMetadata(EventType.ORDER_CANCELLED, "1.0"),
                new OrderCancelledPayload(orderId, itemsDTO)
        );
        String payloadJson = serializeEvent(event);

        OutboxMessage outbox = new OutboxMessage(
                "PEDIDO",
                orderId,
                "orders-cancelled",
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

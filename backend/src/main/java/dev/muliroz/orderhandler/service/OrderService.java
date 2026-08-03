package dev.muliroz.orderhandler.service;

import dev.muliroz.orderhandler.domain.actions.order.CancelOrder;
import dev.muliroz.orderhandler.domain.actions.order.CreateOrder;
import dev.muliroz.orderhandler.domain.actions.order.ListOrders;
import dev.muliroz.orderhandler.domain.entities.Order;
import dev.muliroz.orderhandler.dto.external.CreateOrderRequest;
import dev.muliroz.orderhandler.dto.external.ListOrdersRequest;
import dev.muliroz.orderhandler.dto.external.ListOrdersResponse;
import dev.muliroz.orderhandler.mappers.OrderMapper;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class OrderService {

    private final CreateOrder createOrder;
    private final ListOrders listOrders;
    private final CancelOrder cancelOrder;
    private final OrderMapper mapper;

    public OrderService(CreateOrder createOrder, ListOrders listOrders, CancelOrder cancelOrder, OrderMapper mapper) {
        this.createOrder = createOrder;
        this.listOrders = listOrders;
        this.cancelOrder = cancelOrder;
        this.mapper = mapper;
    }

    public ListOrdersResponse search(ListOrdersRequest request) {
        Map<String, Object> filter = new HashMap<>();

        filter.put("startDate", request.startDate());
        filter.put("endDate", request.endDate());
        filter.put("page", request.page());
        filter.put("size", request.size());
        filter.put("sortBySubtotalDesc", request.sortBySubtotalDesc());

        List<Order> orders = listOrders.execute(filter);
        return new ListOrdersResponse(orders.stream().map(mapper::toDTO).toList());
    }

    @Transactional
    public void create(CreateOrderRequest request) {
        createOrder.execute(request.clientId(), request.orderItems());
    }

    public void cancel(UUID orderId) {
        cancelOrder.execute(orderId);
    }
}

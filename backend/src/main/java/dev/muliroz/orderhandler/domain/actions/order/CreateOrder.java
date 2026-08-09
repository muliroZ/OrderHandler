package dev.muliroz.orderhandler.domain.actions.order;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.contracts.OrderRepository;
import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.domain.entities.Order;
import dev.muliroz.orderhandler.domain.entities.OrderItem;
import dev.muliroz.orderhandler.domain.exceptions.ResourceNotExistsException;
import dev.muliroz.orderhandler.dto.internal.OrderItemDTO;

import java.util.List;
import java.util.UUID;

public class CreateOrder {

    private final OrderRepository orderRepository;
    private final ItemRepository itemRepository;

    public CreateOrder(OrderRepository orderRepository, ItemRepository itemRepository) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
    }

    public Order execute(UUID clientId, List<OrderItemDTO> items) {
        List<OrderItem> orderItems = items.stream()
                .map(dto -> {
                    Item item = itemRepository.findById(dto.itemId())
                            .orElseThrow(() -> new ResourceNotExistsException("O item não existe"));
                    return OrderItem.create(item, dto.quantity());
                })
                .toList();

        Order order = Order.create(clientId, orderItems);
        orderRepository.save(order);

        return order;
    }
}

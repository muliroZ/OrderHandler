package dev.muliroz.orderhandler.config;

import dev.muliroz.orderhandler.domain.actions.order.CancelOrder;
import dev.muliroz.orderhandler.domain.actions.order.CreateOrder;
import dev.muliroz.orderhandler.domain.actions.order.ListOrders;
import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.contracts.OrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OrderActionsConfig {

    @Bean
    CreateOrder createOrder(OrderRepository orderRepository, ItemRepository itemRepository) {
        return new CreateOrder(orderRepository, itemRepository);
    }

    @Bean
    CancelOrder cancelOrder(OrderRepository orderRepository) {
        return new CancelOrder(orderRepository);
    }

    @Bean
    ListOrders listOrders(OrderRepository orderRepository) {
        return new ListOrders(orderRepository);
    }
}

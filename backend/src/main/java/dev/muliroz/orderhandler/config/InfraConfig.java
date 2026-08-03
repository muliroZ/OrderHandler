package dev.muliroz.orderhandler.config;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.contracts.OrderRepository;
import dev.muliroz.orderhandler.mappers.ItemMapper;
import dev.muliroz.orderhandler.mappers.OrderMapper;
import dev.muliroz.orderhandler.mappers.OrderItemMapper;
import dev.muliroz.orderhandler.repository.JpaItemRepository;
import dev.muliroz.orderhandler.repository.JpaOrderRepository;
import dev.muliroz.orderhandler.repository.SpringDataItemRepository;
import dev.muliroz.orderhandler.repository.SpringDataOrderRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class InfraConfig {

    @Bean
    OrderRepository orderRepository(
            SpringDataOrderRepository springDataOrderRepository,
            SpringDataItemRepository springDataItemRepository,
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper
    ) {
        return new JpaOrderRepository(
                springDataOrderRepository,
                springDataItemRepository,
                orderMapper,
                orderItemMapper
        );
    }

    @Bean
    ItemRepository itemRepository(SpringDataItemRepository springDataItemRepository, ItemMapper mapper) {
        return new JpaItemRepository(springDataItemRepository, mapper);
    }
}

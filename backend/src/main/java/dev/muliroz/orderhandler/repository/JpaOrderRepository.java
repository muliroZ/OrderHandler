package dev.muliroz.orderhandler.repository;

import dev.muliroz.orderhandler.domain.contracts.OrderRepository;
import dev.muliroz.orderhandler.domain.entities.Order;
import dev.muliroz.orderhandler.domain.exceptions.ResourceNotExistsException;
import dev.muliroz.orderhandler.mappers.OrderMapper;
import dev.muliroz.orderhandler.mappers.OrderItemMapper;
import dev.muliroz.orderhandler.model.OrderEntity;
import dev.muliroz.orderhandler.model.OrderItemEntity;
import dev.muliroz.orderhandler.repository.utils.OrderSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Component
public class JpaOrderRepository implements OrderRepository {

    private final SpringDataOrderRepository orderRepository;
    private final SpringDataItemRepository itemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    public JpaOrderRepository(
            SpringDataOrderRepository orderRepository,
            SpringDataItemRepository itemRepository,
            OrderMapper orderMapper,
            OrderItemMapper orderItemMapper
    ) {
        this.orderRepository = orderRepository;
        this.itemRepository = itemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    @Override
    public void save(Order order) {
        OrderEntity entity = orderMapper.toEntity(order);

        List<OrderItemEntity> items = order.getOrderItems().stream()
                        .map(orderItem -> orderItemMapper.toEntity(
                                orderItem,
                                entity,
                                itemRepository.getReferenceById(orderItem.item().getId())
                        ))
                        .toList();

        entity.setOrderItems(items);
        orderRepository.save(entity);
    }

    @Override
    public List<Order> search(Map<String, Object> filter) {
        Specification<OrderEntity> spec = Specification.unrestricted();

        if (filter.get("startDate") instanceof LocalDateTime startDate) {
            spec = spec.and(OrderSpecification.createdAfter(startDate));
        }

        if (filter.get("endDate") instanceof LocalDateTime endDate) {
            spec = spec.and(OrderSpecification.createdBefore(endDate));
        }

        Sort sort = Sort.unsorted();
        if (filter.get("sortBySubtotalDesc") instanceof Boolean sortBySubtotalDesc) {
            sort = sortBySubtotalDesc
                    ? Sort.by(Sort.Direction.DESC, "subtotal")
                    : Sort.by(Sort.Direction.DESC, "createdAt");
        }

        int page = filter.get("page") != null
                ? (Integer) filter.get("page")
                : 0;

        int size = filter.get("size") != null
                ? (Integer) filter.get("size")
                : 10;

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<OrderEntity> resultPage = orderRepository.findAll(spec, pageable);

        return resultPage.get().map(orderMapper::toDomain).toList();
    }

    @Override
    public Optional<Order> findById(UUID orderId) {
        OrderEntity entity = orderRepository.findById(orderId)
                .orElseThrow(() -> new ResourceNotExistsException("O pedido não existe"));

        Order order = orderMapper.toDomain(entity);
        return Optional.of(order);
    }
}

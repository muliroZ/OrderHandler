package dev.muliroz.orderhandler.repository.utils;

import dev.muliroz.orderhandler.model.OrderEntity;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDateTime;

public class OrderSpecification {

    public static Specification<OrderEntity> createdAfter(LocalDateTime startDate) {
        return (root, query, cb) ->
                startDate != null
                        ? cb.greaterThanOrEqualTo(root.get("createdAt"), startDate)
                        : cb.conjunction();
    }

    public static Specification<OrderEntity> createdBefore(LocalDateTime endDate) {
        return (root, query, cb) ->
                endDate != null
                        ? cb.lessThanOrEqualTo(root.get("createdAt"), endDate)
                        : cb.conjunction();
    }
}

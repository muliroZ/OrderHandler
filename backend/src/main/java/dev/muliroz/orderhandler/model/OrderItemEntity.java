package dev.muliroz.orderhandler.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "order_items")
@EqualsAndHashCode(of = "id")
public class OrderItemEntity {

    @Id
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "order_id")
    private OrderEntity order;

    @ManyToOne
    @JoinColumn(name = "item_id")
    private ItemEntity item;

    @Column(nullable = false)
    private int quantity;

    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal unitPrice;
}

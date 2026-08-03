package dev.muliroz.orderhandler.model;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;
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
@Table(name = "items")
@EqualsAndHashCode(of =  "id")
public class ItemEntity {

    @Id
    private UUID id;

    @Column(length = 75, nullable = false)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, precision = 11, scale = 2)
    private BigDecimal price;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ItemCategory category;

    @Column(nullable = false)
    private int stock;

    @Column(nullable = false)
    private boolean active;

    @PrePersist
    private void onCreate() {
        this.active = true;
    }
}

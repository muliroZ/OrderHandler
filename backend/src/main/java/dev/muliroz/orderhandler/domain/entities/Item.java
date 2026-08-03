package dev.muliroz.orderhandler.domain.entities;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;
import dev.muliroz.orderhandler.domain.exceptions.InvalidNumberException;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.UUID;

public class Item {

    private final UUID id;
    private final String name;
    private final String description;
    private final BigDecimal price;
    private final ItemCategory category;
    private final int stock;
    private final boolean active;

    private Item(
            String name,
            String description,
            BigDecimal price,
            ItemCategory category,
            int stock
    ) {
        if (name.isBlank() || name.length() > 75) {
            throw new IllegalArgumentException();
        }

        if (price.compareTo(BigDecimal.ZERO) <= 0) {
            throw new InvalidNumberException("O preço deve ser maior que 0 (zero).");
        }

        if (!Arrays.stream(ItemCategory.values()).toList().contains(category)) {
            category = ItemCategory.OUTROS;
        }

        if (stock < 0) {
            throw new InvalidNumberException("O estoque deve ser maior ou igual a 0 (zero).");
        }

        this.id = UUID.randomUUID();
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.active = true;
    }

    private Item(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            ItemCategory category,
            int stock,
            boolean active
    ) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.category = category;
        this.stock = stock;
        this.active = active;
    }

    public static Item create(
            String name,
            String description,
            BigDecimal price,
            ItemCategory category,
            int stock
    ) {
        return new Item(
                name, description, price, category, stock
        );
    }

    public static Item restore(
            UUID id,
            String name,
            String description,
            BigDecimal price,
            ItemCategory category,
            int stock,
            boolean active
    ) {
        return new Item(id, name, description, price, category, stock, active);
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public ItemCategory getCategory() {
        return category;
    }

    public int getStock() {
        return stock;
    }

    public boolean isActive() {
        return active;
    }
}

package dev.muliroz.orderhandler.domain.entities;

import dev.muliroz.orderhandler.domain.exceptions.InvalidNumberException;

import java.math.BigDecimal;
import java.util.UUID;

public class OrderItem {

    private final UUID id;
    private final Item item;
    private final int quantity;
    private final BigDecimal unitPrice;

    private OrderItem(Item item, int quantity) {
        if (quantity <= 0) {
            throw new InvalidNumberException("Quantidade deve ser positiva");
        }

        this.id = UUID.randomUUID();
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = item.getPrice();
    }

    private OrderItem(UUID id, Item item, int quantity, BigDecimal unitPrice) {
        this.id = id;
        this.item = item;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
    }

    public static OrderItem create(Item item, int quantity) {
        return new OrderItem(item, quantity);
    }

    public static OrderItem restore(UUID id, Item item, int quantity, BigDecimal unitPrice) {
        return new OrderItem(id, item, quantity, unitPrice);
    }

    public BigDecimal subtotal() {
        return unitPrice.multiply(
                BigDecimal.valueOf(quantity)
        );
    }

    public UUID id() {
        return id;
    }

    public Item item() {
        return item;
    }

    public int quantity() {
        return quantity;
    }

    public BigDecimal unitPrice() {
        return unitPrice;
    }
}

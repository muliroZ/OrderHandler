package dev.muliroz.orderhandler.domain.contracts;

import dev.muliroz.orderhandler.domain.entities.Item;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public interface ItemRepository {
    void save(Item item);
    List<Item> search(Map<String, Object> filter);
    Optional<Item> findById(UUID itemId);
    boolean isUsed(UUID itemId);
}

package dev.muliroz.orderhandler.repository;

import dev.muliroz.orderhandler.domain.contracts.ItemRepository;
import dev.muliroz.orderhandler.domain.entities.Item;
import dev.muliroz.orderhandler.domain.enums.ItemCategory;
import dev.muliroz.orderhandler.domain.exceptions.ResourceNotExistsException;
import dev.muliroz.orderhandler.mappers.ItemMapper;
import dev.muliroz.orderhandler.model.ItemEntity;
import dev.muliroz.orderhandler.repository.utils.ItemSpecification;
import dev.muliroz.orderhandler.repository.utils.SortBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.*;

@Component
public class JpaItemRepository implements ItemRepository {

    private final SpringDataItemRepository itemRepository;
    private final ItemMapper mapper;

    public JpaItemRepository(SpringDataItemRepository itemRepository, ItemMapper mapper) {
        this.itemRepository = itemRepository;
        this.mapper = mapper;
    }

    @Override
    public void save(Item item) {
        ItemEntity entity = mapper.toEntity(item);
        itemRepository.save(entity);
    }

    @Override
    public List<Item> search(Map<String, Object> filter) {
        Specification<ItemEntity> spec = Specification.unrestricted();

        if (filter.get("term") instanceof String term && !term.isBlank()) {
            spec = spec.and(ItemSpecification.nameContains(term));
        }

        if (filter.get("minPrice") instanceof BigDecimal minPrice) {
            spec = spec.and(ItemSpecification.priceGreaterThanOrEqualTo(minPrice));
        }

        if (filter.get("maxPrice") instanceof BigDecimal maxPrice) {
            spec = spec.and(ItemSpecification.priceLesserThanOrEqualTo(maxPrice));
        }

        if (filter.get("category") instanceof ItemCategory category
                && Arrays.asList(ItemCategory.values()).contains(category)
        ) {
            spec = spec.and(ItemSpecification.byCategory(category));
        }

        if (filter.get("byStock") instanceof Boolean byStock) {
            spec = spec.and(ItemSpecification.hasStock(byStock));
        }

        Sort sort = Sort.unsorted();
        if (filter.get("ordinations") instanceof String[] ordinations && ordinations.length > 0) {
            sort = SortBuilder.createMultipleSorting(ordinations);
        }

        int page = filter.get("page") != null
                ? (Integer) filter.get("page")
                : 0;

        int size = filter.get("size") != null
                ? (Integer) filter.get("size")
                : 10;

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ItemEntity> resultPage = itemRepository.findAll(spec, pageable);

        return resultPage.get().filter(ItemEntity::isActive).map(mapper::toDomain).toList();
    }

    @Override
    public Optional<Item> findById(UUID itemId) {
        ItemEntity entity = itemRepository.findById(itemId)
                .orElseThrow(() -> new ResourceNotExistsException("O item não existe"));

        return Optional.of(mapper.toDomain(entity));
    }

    @Override
    public boolean isUsed(UUID itemId) {
        return itemRepository.isUsed(itemId);
    }
}

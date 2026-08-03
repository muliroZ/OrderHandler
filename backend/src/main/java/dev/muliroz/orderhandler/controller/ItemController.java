package dev.muliroz.orderhandler.controller;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;
import dev.muliroz.orderhandler.dto.external.CreateItemRequest;
import dev.muliroz.orderhandler.dto.external.ListItemsRequest;
import dev.muliroz.orderhandler.dto.external.ListItemsResponse;
import dev.muliroz.orderhandler.dto.external.UpdateItemRequest;
import dev.muliroz.orderhandler.service.ItemService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/items")
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping("/search")
    public ResponseEntity<ListItemsResponse> search(
            @RequestParam(required = false) String term,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @RequestParam(required = false) ItemCategory category,
            @RequestParam(defaultValue = "false") boolean byStock,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String[] ordinations
    ) {
        ListItemsResponse response = itemService.search(new ListItemsRequest(
                term, minPrice, maxPrice, category, byStock, page, size, ordinations
        ));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateItemRequest request) {
        itemService.create(request);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Void> update(@PathVariable UUID itemId, @Valid @RequestBody UpdateItemRequest request) {
        itemService.update(itemId, request);
        return ResponseEntity.status(200).build();
    }

    @DeleteMapping("/{itemId}")
    public ResponseEntity<Void> delete(@PathVariable UUID itemId) {
        itemService.delete(itemId);
        return ResponseEntity.status(204).build();
    }
}

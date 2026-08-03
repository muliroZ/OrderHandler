package dev.muliroz.orderhandler.controller;

import dev.muliroz.orderhandler.dto.external.CreateOrderRequest;
import dev.muliroz.orderhandler.dto.external.ListOrdersRequest;
import dev.muliroz.orderhandler.dto.external.ListOrdersResponse;
import dev.muliroz.orderhandler.service.OrderService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/orders")
public class OrderController {

    private final OrderService orderService;

    public OrderController(OrderService orderService) {
        this.orderService = orderService;
    }

    @GetMapping("/search")
    public ResponseEntity<ListOrdersResponse> search(
            @RequestParam(required = false) LocalDateTime startDate,
            @RequestParam(required = false) LocalDateTime endDate,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "false") boolean sortBySubtotalAsc
    ) {
        ListOrdersResponse response = orderService.search(new ListOrdersRequest(
                startDate, endDate, page, size, sortBySubtotalAsc
        ));
        return ResponseEntity.status(200).body(response);
    }

    @PostMapping
    public ResponseEntity<Void> create(@Valid @RequestBody CreateOrderRequest request) {
        orderService.create(request);
        return ResponseEntity.status(201).build();
    }

    @PatchMapping("/{orderId}")
    public ResponseEntity<Void> cancel(@PathVariable UUID orderId) {
        orderService.cancel(orderId);
        return ResponseEntity.status(200).build();
    }
}

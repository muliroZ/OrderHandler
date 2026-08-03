package dev.muliroz.orderhandler.dto.external;

import dev.muliroz.orderhandler.dto.internal.ItemDTO;

import java.util.List;

public record ListItemsResponse(
        List<ItemDTO> items
) {
}

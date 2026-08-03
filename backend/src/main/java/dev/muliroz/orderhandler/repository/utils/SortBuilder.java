package dev.muliroz.orderhandler.repository.utils;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;

import java.util.ArrayList;
import java.util.List;

public class SortBuilder {

    public static Sort createMultipleSorting(String... args) {
        List<Order> ordenationParams = new ArrayList<>();

        for (String field : args) {
            // formato => {"x,asc" (ASC), "y,desc" (DESC)} || {"x" (ASC)}
            if (field.contains(",")) {
                String[] parts = field.split(",");
                String fieldName = parts[0].trim();
                String direction = parts[1].trim();

                if ("desc".equalsIgnoreCase(direction)) {
                    ordenationParams.add(Order.desc(fieldName));
                } else {
                    ordenationParams.add(Order.asc(fieldName));
                }
            } else {
                ordenationParams.add(Order.asc(field.trim()));
            }
        }
        return Sort.by(ordenationParams);
    }
}

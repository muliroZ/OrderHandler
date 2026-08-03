package dev.muliroz.orderhandler.repository.utils;

import dev.muliroz.orderhandler.domain.enums.ItemCategory;
import dev.muliroz.orderhandler.model.ItemEntity;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

public class ItemSpecification {

    public static Specification<ItemEntity> nameContains(String term) {
        return (root, query, cb) -> {
            if (term == null || term.trim().isBlank()) {
                return cb.conjunction();
            }

            Expression<String> nameCoalesce = cb.coalesce(root.get("name"), "");
            Expression<String> descriptionCoalesce = cb.coalesce(root.get("description"), "");

            var vectorName = cb.function("setweight", Object.class,
                    cb.function("to_tsvector", Object.class, cb.literal("portuguese"), nameCoalesce),
                    cb.literal('A')
            );

            var vectorDescription = cb.function("setweight", Object.class,
                    cb.function("to_tsvector", Object.class, cb.literal("portuguese"), descriptionCoalesce),
                    cb.literal('B')
            );

            var tsvector = cb.function("tsvector_concat", Object.class, vectorName, vectorDescription);

            var tsquery = cb.function("to_tsquery", Object.class,
                    cb.literal("portuguese"), cb.literal(formatTerm(term)));

            if (query.getResultType() != Long.class && query.getResultType() != long.class) {
                var tsrank = cb.function("ts_rank", Double.class, tsvector, tsquery);
                query.orderBy(cb.desc(tsrank));
            }

            return cb.isTrue(
                    cb.function("fts_match", Boolean.class, tsvector, tsquery)
            );
        };
    }

    public static Specification<ItemEntity> priceGreaterThanOrEqualTo(BigDecimal minPrice) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(
                        root.get("price"),
                        minPrice != null ? minPrice : BigDecimal.ZERO
                );
    }

    public static Specification<ItemEntity> priceLesserThanOrEqualTo(BigDecimal maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(
                        root.get("price"),
                        maxPrice != null ? maxPrice : BigDecimal.valueOf(Long.MAX_VALUE)
                );
    }

    public static Specification<ItemEntity> byCategory(ItemCategory category) {
        return (root, query, cb) ->
                category != null
                        ? cb.equal(root.get("category"), category)
                        : cb.conjunction();
    }

    public static Specification<ItemEntity> hasStock(boolean byStock) {
        return (root, query, cb) ->
            byStock
                    ? cb.greaterThan(root.get("stock"), 0)
                    : cb.conjunction();
    }

    // AUX METHOD
    private static String formatTerm(String term) {
        String clean = term.replaceAll("[^a-zA-Z0-9\\s]", "").trim();
        if (clean.isEmpty()) return "";

        String[] words = clean.split("\\s+");
        StringBuilder queryBuilder = new StringBuilder();

        for (int i = 0; i < words.length; i++) {
            queryBuilder.append(words[i]).append(":*");
            if (i < words.length - 1) {
                queryBuilder.append(" & ");
            }
        }
        return queryBuilder.toString();
    }
}

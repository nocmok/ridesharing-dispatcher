package com.nocmok.orp.postgres.storage.filter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class Filter {

    private final List<OneOf<?, ?>> oneOfClauses = new ArrayList<>();
    private final List<OrderBy<?, ?>> orderByClauses = new ArrayList<>();
    private final Supplier<Long> identityGenerator = new Supplier<>() {
        private final Iterator<Long> iterator = new Iterator<>() {
            private long next = 1;

            @Override public boolean hasNext() {
                return true;
            }

            @Override public Long next() {
                return next++;
            }
        };

        @Override public Long get() {
            return iterator.next();
        }
    };
    private final Map<String, Object> paramsMap = new HashMap<>();
    private Long pageSize = 100L;
    private Long page = 0L;

    public <F, V> Filter oneOf(Field<F, V> field, List<V> values) {
        oneOfClauses.add(new OneOf<>(field, values));
        return this;
    }

    public <F, V> Filter orderBy(Field<F, V> field, boolean ascending) {
        orderByClauses.add(new OrderBy<>(field, ascending));
        return this;
    }

    public Filter page(long page) {
        this.page = page;
        return this;
    }

    public Filter pageSize(long pageSize) {
        this.pageSize = pageSize;
        return this;
    }

    public Optional<String> getWhereString() {
        if (oneOfClauses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(oneOfClauses.stream()
                .map(oneOf -> oneOf.getClause(identityGenerator, paramsMap)).map(str -> "(" + str + ")")
                .collect(Collectors.joining(" and ")));
    }

    public Optional<String> getOrderByClause() {
        if (orderByClauses.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(orderByClauses.stream()
                .map(OrderBy::getClause)
                .collect(Collectors.joining(",")));
    }

    public String applyPaging(String request) {
        return " select * from (select ___t1.*, ((row_number() over (" + getOrderByClause().map("order by "::concat).orElse("") +
                ")) - 1) as ___rn from (" + request +
                ") as ___t1) as ___t2 where ___rn >= " + (page * pageSize) +
                " and ___rn < " + (page * pageSize + pageSize) + " order by ___rn asc";
    }

    public Map<String, Object> getParamsMap() {
        return paramsMap;
    }

    private static class OneOf<F, V> {
        private final Field<F, V> field;
        private final List<V> values;

        public OneOf(Field<F, V> field, List<V> values) {
            this.field = field;
            this.values = values;
        }

        public String getClause(Supplier<Long> identityGenerator, Map<String, Object> paramsMap) {
            if (values == null || values.isEmpty()) {
                return "";
            }
            var valueMap = values.stream().collect(Collectors.toMap(value -> "___" + field.getFieldName() + identityGenerator.get(), field::convertValue));
            paramsMap.putAll(valueMap);
            return field.getFieldName() + " in (" + valueMap.keySet().stream()
                    .map(identity -> ":" + identity)
                    .collect(Collectors.joining(",")) + ")";
        }
    }

    private static class OrderBy<F, V> {
        private final Field<F, V> field;
        private final boolean ascending;

        public OrderBy(Field<F, V> field, boolean ascending) {
            this.field = field;
            this.ascending = ascending;
        }

        public String getClause() {
            return field.getFieldName() + (ascending ? " asc" : " desc");
        }
    }
}

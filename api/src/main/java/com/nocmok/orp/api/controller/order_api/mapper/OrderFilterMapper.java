package com.nocmok.orp.api.controller.order_api.mapper;

import com.nocmok.orp.api.controller.common_dto.OneOf;
import com.nocmok.orp.api.controller.common_dto.OrderBy;
import com.nocmok.orp.api.controller.common_dto.RequestFilter;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.postgres.storage.filter.Filter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.time.Instant;
import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class OrderFilterMapper {

    private void parseOneOfClauses(Filter filter, RequestFilter requestFilter) {
        for (var oneOf : Objects.requireNonNullElse(requestFilter.getFiltering(), Collections.<OneOf>emptyList())) {
            if (CollectionUtils.isEmpty(oneOf.getValues())) {
                continue;
            }
            switch (oneOf.getFieldName()) {
                case "orderId":
                    filter.oneOf(ServiceRequest.Fields.requestId, oneOf.getValues());
                    break;
                case "status":
                    filter.oneOf(ServiceRequest.Fields.status, oneOf.getValues().stream().map(OrderStatus::valueOf).collect(Collectors.toList()));
                    break;
                case "requestedAt":
                    filter.oneOf(ServiceRequest.Fields.requestedAt,
                            oneOf.getValues().stream().map(value -> value == null ? null : Instant.parse(value)).collect(Collectors.toList()));
                    break;
                case "completedAt":
                    filter.oneOf(ServiceRequest.Fields.completedAt,
                            oneOf.getValues().stream().map(value -> value == null ? null : Instant.parse(value)).collect(Collectors.toList()));
                    break;
                case "servingSessionId":
                    filter.oneOf(ServiceRequest.Fields.servingSessionId, oneOf.getValues());
                    break;
                default:
            }
        }
    }

    private void parseOrderByClauses(Filter filter, RequestFilter requestFilter) {
        for (var orderBy : Objects.requireNonNullElse(requestFilter.getOrdering(), Collections.<OrderBy>emptyList())) {
            switch (orderBy.getFieldName()) {
                case "orderId":
                    filter.orderBy(ServiceRequest.Fields.requestId, orderBy.isAscending());
                    break;
                case "status":
                    filter.orderBy(ServiceRequest.Fields.status, orderBy.isAscending());
                    break;
                case "orderedAt":
                    filter.orderBy(ServiceRequest.Fields.requestedAt, orderBy.isAscending());
                    break;
                case "servingSessionId":
                    filter.orderBy(ServiceRequest.Fields.servingSessionId, orderBy.isAscending());
                    break;
                default:
            }
        }
    }

    public Filter mapRequestFilterToInternalFilter(RequestFilter requestFilter) {
        var filter = new Filter();
        parseOneOfClauses(filter, requestFilter);
        parseOrderByClauses(filter, requestFilter);
        filter.page(requestFilter.getPage());
        filter.pageSize(requestFilter.getPageSize());
        return filter;
    }
}

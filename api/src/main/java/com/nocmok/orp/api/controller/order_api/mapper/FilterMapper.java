package com.nocmok.orp.api.controller.order_api.mapper;

import com.nocmok.orp.api.controller.common_dto.RequestFilter;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.postgres.storage.filter.Filter;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.stream.Collectors;

@Component
public class FilterMapper {

    private void parseOneOfClauses(Filter filter, RequestFilter requestFilter) {
        for (var oneOf : requestFilter.getFiltering()) {
            switch (oneOf.getFieldName()) {
                case "orderId":
                    filter.oneOf(ServiceRequest.Fields.requestId, oneOf.getValues());
                    break;
                case "status":
                    filter.oneOf(ServiceRequest.Fields.status, oneOf.getValues().stream().map(OrderStatus::valueOf).collect(Collectors.toList()));
                    break;
                case "requestedAt":
                    filter.oneOf(ServiceRequest.Fields.requestedAt, oneOf.getValues().stream().map(Instant::parse).collect(Collectors.toList()));
                    break;
                case "servingSessionId":
                    filter.oneOf(ServiceRequest.Fields.servingSessionId, oneOf.getValues());
                    break;
                default:
            }
        }
    }

    private void parseOrderByClauses(Filter filter, RequestFilter requestFilter) {
        for (var orderBy : requestFilter.getOrdering()) {
            switch (orderBy.getFieldName()) {
                case "orderId":
                    filter.orderBy(ServiceRequest.Fields.requestId, orderBy.isAscending());
                    break;
                case "status":
                    filter.orderBy(ServiceRequest.Fields.status, orderBy.isAscending());
                    break;
                case "requestedAt":
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
        return filter;
    }
}

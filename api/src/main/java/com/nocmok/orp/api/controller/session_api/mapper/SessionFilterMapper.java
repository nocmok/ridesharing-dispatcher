package com.nocmok.orp.api.controller.session_api.mapper;

import com.nocmok.orp.api.controller.common_dto.OneOf;
import com.nocmok.orp.api.controller.common_dto.OrderBy;
import com.nocmok.orp.api.controller.common_dto.RequestFilter;
import com.nocmok.orp.postgres.storage.dto.Session;
import com.nocmok.orp.postgres.storage.dto.SessionStatus;
import com.nocmok.orp.postgres.storage.filter.Filter;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.Collections;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
public class SessionFilterMapper {

    private void parseOneOfClauses(Filter filter, RequestFilter requestFilter) {
        for (var oneOf : Objects.requireNonNullElse(requestFilter.getFiltering(), Collections.<OneOf>emptyList())) {
            if (CollectionUtils.isEmpty(oneOf.getValues())) {
                continue;
            }
            switch (oneOf.getFieldName()) {
                case "sessionId":
                    filter.oneOf(Session.Fields.sessionId, oneOf.getValues().stream().map(Long::parseLong).collect(Collectors.toList()));
                    break;
                case "totalCapacity":
                    filter.oneOf(Session.Fields.totalCapacity, oneOf.getValues().stream().map(Integer::parseInt).collect(Collectors.toList()));
                    break;
                case "residualCapacity":
                    filter.oneOf(Session.Fields.residualCapacity, oneOf.getValues().stream().map(Integer::parseInt).collect(Collectors.toList()));
                    break;
                case "status":
                    filter.oneOf(Session.Fields.status, oneOf.getValues().stream().map(SessionStatus::valueOf).collect(Collectors.toList()));
                    break;
                default:
            }
        }
    }

    private void parseOrderByClauses(Filter filter, RequestFilter requestFilter) {
        for (var orderBy : Objects.requireNonNullElse(requestFilter.getOrdering(), Collections.<OrderBy>emptyList())) {
            switch (orderBy.getFieldName()) {
                case "sessionId":
                    filter.orderBy(Session.Fields.sessionId, orderBy.isAscending());
                    break;
                case "totalCapacity":
                    filter.orderBy(Session.Fields.totalCapacity, orderBy.isAscending());
                    break;
                case "residualCapacity":
                    filter.orderBy(Session.Fields.residualCapacity, orderBy.isAscending());
                    break;
                case "status":
                    filter.orderBy(Session.Fields.status, orderBy.isAscending());
                    break;
                case "startedAt":
                    filter.orderBy(Session.Fields.startedAt, orderBy.isAscending());
                    break;
                case "terminatedAt":
                    filter.orderBy(Session.Fields.terminatedAt, orderBy.isAscending());
                    break;
                default:
            }
        }
    }

    private void parsePaging(Filter filter, RequestFilter requestFilter) {
        filter.page(Objects.requireNonNullElse(requestFilter.getPage(), 0L));
        filter.pageSize(Objects.requireNonNullElse(requestFilter.getPageSize(), 100L));
    }

    public Filter mapRequestFilterToInternalFilter(RequestFilter requestFilter) {
        var filter = new Filter();
        parseOneOfClauses(filter, requestFilter);
        parseOrderByClauses(filter, requestFilter);
        parsePaging(filter, requestFilter);
        return filter;
    }
}

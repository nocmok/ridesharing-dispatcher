package com.nocmok.orp.api.service.request;

import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.postgres.storage.filter.Filter;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    List<String> getActiveRequestIds();

    Optional<ServiceRequest> getRequestInfo(String requestId);

    List<ServiceRequest.OrderStatusLogEntry> getOrderStatusLog(String orderId, int page, int entriesPerPage, boolean ascending);

    List<ServiceRequest> getOrders(Filter filter);

    void cancelOrder(String orderId);
}

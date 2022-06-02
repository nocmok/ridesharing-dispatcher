package com.nocmok.orp.api.service.request;

import com.nocmok.orp.api.common.ResourceNotFoundException;
import com.nocmok.orp.api.service.session.SessionManagementService;
import com.nocmok.orp.postgres.storage.ServiceRequestStorage;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import com.nocmok.orp.postgres.storage.filter.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private ServiceRequestStorage requestInfoStorage;
    private SessionManagementService sessionManagementService;

    @Autowired
    public RequestServiceImpl(ServiceRequestStorage requestInfoStorage, SessionManagementService sessionManagementService) {
        this.requestInfoStorage = requestInfoStorage;
        this.sessionManagementService = sessionManagementService;
    }

    @Override public List<String> getActiveRequestIds() {
        return requestInfoStorage.getActiveRequestsIds();
    }

    @Override public Optional<ServiceRequest> getRequestInfo(String requestId) {
        return requestInfoStorage.getRequestById(requestId);
    }

    @Override public List<ServiceRequest.OrderStatusLogEntry> getOrderStatusLog(String orderId, int page, int entriesPerPage, boolean ascending) {
        return requestInfoStorage.getOrderStatusLog(Long.parseLong(orderId), page, entriesPerPage, ascending);
    }

    @Override public List<ServiceRequest> getOrders(Filter filter) {
        // validate filter
        return requestInfoStorage.getOrders(filter);
    }

    @Transactional
    @Override public void cancelOrder(String orderId) {
        var order = requestInfoStorage.getRequestByIdForUpdate(orderId).orElseThrow(ResourceNotFoundException::new);
        if (order.getServingSessionId() == null) {
            if(order.getCompletedAt() != null) {
                return;
            }
            requestInfoStorage.updateRequestStatus(orderId, OrderStatus.CANCELLED, true);
        } else {
            sessionManagementService.updateOrderStatus(order.getServingSessionId(), orderId, com.nocmok.orp.kafka.orp_input.OrderStatus.CANCELLED);
        }
    }
}

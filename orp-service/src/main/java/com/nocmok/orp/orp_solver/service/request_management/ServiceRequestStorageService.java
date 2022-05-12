package com.nocmok.orp.orp_solver.service.request_management;

import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;

import java.util.Optional;

public interface ServiceRequestStorageService {

    Optional<ServiceRequest> getRequestById(String id);

    Optional<ServiceRequest> getRequestByIdForUpdate(String id);

    void updateRequestStatus(String requestId, OrderStatus status);

    void updateServingSessionId(String requestId, String sessionId);
}

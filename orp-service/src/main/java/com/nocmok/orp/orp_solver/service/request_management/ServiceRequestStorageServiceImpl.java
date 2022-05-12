package com.nocmok.orp.orp_solver.service.request_management;

import com.nocmok.orp.postgres.storage.ServiceRequestStorage;
import com.nocmok.orp.postgres.storage.dto.OrderStatus;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceRequestStorageServiceImpl implements ServiceRequestStorageService {

    private final ServiceRequestStorage serviceRequestStorage;

    @Autowired
    public ServiceRequestStorageServiceImpl(ServiceRequestStorage serviceRequestStorage) {
        this.serviceRequestStorage = serviceRequestStorage;
    }

    @Override
    public Optional<ServiceRequest> getRequestById(String id) {
        return serviceRequestStorage.getRequestById(id);
    }

    @Override public Optional<ServiceRequest> getRequestByIdForUpdate(String id) {
        return serviceRequestStorage.getRequestByIdForUpdate(id);
    }

    @Override public void updateRequestStatus(String requestId, OrderStatus status) {
        serviceRequestStorage.updateRequestStatus(requestId, status);
    }

    @Override public void updateServingSessionId(String requestId, String sessionId) {
        serviceRequestStorage.updateServingSessionId(requestId, sessionId);
    }
}

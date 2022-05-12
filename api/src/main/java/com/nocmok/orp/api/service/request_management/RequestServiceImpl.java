package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.postgres.storage.ServiceRequestStorage;
import com.nocmok.orp.postgres.storage.dto.ServiceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private ServiceRequestStorage requestInfoStorage;

    @Autowired
    public RequestServiceImpl(ServiceRequestStorage requestInfoStorage) {
        this.requestInfoStorage = requestInfoStorage;
    }

    @Override public List<String> getActiveRequestIds() {
        return requestInfoStorage.getActiveRequestsIds();
    }

    @Override public Optional<ServiceRequest> getRequestInfo(String requestId) {
        return requestInfoStorage.getRequestById(requestId);
    }
}

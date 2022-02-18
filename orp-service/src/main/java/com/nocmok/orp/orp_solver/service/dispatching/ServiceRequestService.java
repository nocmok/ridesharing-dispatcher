package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.core_api.Request;
import com.nocmok.orp.orp_solver.storage.dispatching.ServiceRequestStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ServiceRequestService {

    private ServiceRequestStorage serviceRequestStorage;

    @Autowired
    public ServiceRequestService(ServiceRequestStorage serviceRequestStorage) {
        this.serviceRequestStorage = serviceRequestStorage;
    }

    public Optional<Request> getRequestById(String id) {
        return serviceRequestStorage.getRequestById(id);
    }

    public void insertRequest(Request request) {
        serviceRequestStorage.insertRequest(request);
    }
}

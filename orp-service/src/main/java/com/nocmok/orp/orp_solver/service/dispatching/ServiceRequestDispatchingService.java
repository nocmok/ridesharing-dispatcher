package com.nocmok.orp.orp_solver.service.dispatching;

import com.nocmok.orp.postgres.storage.dto.ServiceRequest;

public interface ServiceRequestDispatchingService {

    void dispatchServiceRequest(ServiceRequest serviceRequest);
}

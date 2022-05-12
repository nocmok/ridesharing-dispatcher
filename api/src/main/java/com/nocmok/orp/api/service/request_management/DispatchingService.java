package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.postgres.storage.dto.ServiceRequest;

public interface DispatchingService {

    ServiceRequest dispatchRequest(ServiceRequest requestInfo);
}

package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;

public interface DispatchingService {

    RequestInfo dispatchRequest(RequestInfo requestInfo);
}

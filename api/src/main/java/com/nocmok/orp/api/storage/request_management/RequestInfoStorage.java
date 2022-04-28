package com.nocmok.orp.api.storage.request_management;

import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;

import java.util.List;
import java.util.Optional;

public interface RequestInfoStorage {

    String getIdForRequest();

    /**
     * status ~ (PENDING, SERVING)
     */
    List<String> getActiveRequestsIds();

    Optional<RequestInfo> getRequestInfo(String requestId);

    RequestInfo storeRequest(RequestInfo requestInfo);
}

package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;

import java.util.List;
import java.util.Optional;

public interface RequestService {

    List<String> getActiveRequestIds();

    Optional<RequestInfo> getRequestInfo(String requestId);
}

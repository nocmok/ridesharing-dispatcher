package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.api.storage.request_management.RequestInfoStorage;
import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RequestServiceImpl implements RequestService {

    private RequestInfoStorage requestInfoStorage;

    @Autowired
    public RequestServiceImpl(RequestInfoStorage requestInfoStorage) {
        this.requestInfoStorage = requestInfoStorage;
    }

    @Override public List<String> getActiveRequestIds() {
        return requestInfoStorage.getActiveRequestsIds();
    }

    @Override public Optional<RequestInfo> getRequestInfo(String requestId) {
        return requestInfoStorage.getRequestInfo(requestId);
    }
}

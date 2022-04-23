package com.nocmok.orp.api.service.request_management;

import com.nocmok.orp.api.storage.request_management.RequestInfoStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
}

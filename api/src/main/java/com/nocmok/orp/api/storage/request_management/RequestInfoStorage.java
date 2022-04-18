package com.nocmok.orp.api.storage.request_management;

import com.nocmok.orp.api.storage.request_management.dto.RequestInfo;

public interface RequestInfoStorage {

    RequestInfo insertRequest(RequestInfo requestInfo);
}

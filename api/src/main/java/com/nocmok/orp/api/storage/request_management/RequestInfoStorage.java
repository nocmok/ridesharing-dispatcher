package com.nocmok.orp.api.storage.request_management;

import java.util.List;

public interface RequestInfoStorage {

    String getIdForRequest();

    /**
     * status ~ (PENDING, SERVING)
     */
    List<String> getActiveRequestsIds();
}

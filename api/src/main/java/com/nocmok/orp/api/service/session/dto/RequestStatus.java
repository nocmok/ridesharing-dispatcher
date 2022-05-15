package com.nocmok.orp.api.service.session.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum RequestStatus {

    @JsonProperty("SERVING")
    SERVING,

    @JsonProperty("SERVED")
    SERVED,
    /**
     * Запрос отклонен сразу
     */
    @JsonProperty("DENIED")
    DENIED,
    /**
     * Запрос отклонен в процессе выполнения (Запрос был в состоянии SERVING)
     */
    @JsonProperty("SERVING_DENIED")
    SERVING_DENIED;
}

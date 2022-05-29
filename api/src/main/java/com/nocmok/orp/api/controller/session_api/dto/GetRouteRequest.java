package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class GetRouteRequest {

    @JsonProperty("sessionId")
    private String sessionId;
}

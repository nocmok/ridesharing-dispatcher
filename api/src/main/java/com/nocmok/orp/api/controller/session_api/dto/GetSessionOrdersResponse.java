package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class GetSessionOrdersResponse {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("orders")
    private List<SessionOrderAssignment> orders;

    @JsonGetter("size")
    public int size() {
        return orders == null ? 0 : orders.size();
    }
}

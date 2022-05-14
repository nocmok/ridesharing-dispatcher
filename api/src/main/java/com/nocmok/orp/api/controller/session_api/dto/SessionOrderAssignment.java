package com.nocmok.orp.api.controller.session_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
@Builder
public class SessionOrderAssignment {

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("assignedAt")
    private Instant assignedAt;
}

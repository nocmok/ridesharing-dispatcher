package com.nocmok.orp.api.controller.driver_api.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.api.service.session_management.dto.RequestStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateScheduleRequest {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("orderId")
    private String orderId;

    @JsonProperty("updatedStatus")
    private RequestStatus updatedStatus;
}

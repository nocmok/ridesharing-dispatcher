package com.nocmok.orp.simulator.service.api;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ServiceRequestConfirmation {

    private String sessionId;
    private String requestId;
    private String reservationId;
}

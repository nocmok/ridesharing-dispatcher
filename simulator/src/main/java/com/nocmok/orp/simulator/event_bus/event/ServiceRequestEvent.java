package com.nocmok.orp.simulator.event_bus.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class ServiceRequestEvent implements Event {

    private String sessionId;
    private String requestId;
    private String reservationId;

    @Override public String getKey() {
        return null;
    }
}

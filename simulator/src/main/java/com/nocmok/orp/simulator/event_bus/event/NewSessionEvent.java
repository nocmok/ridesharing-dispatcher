package com.nocmok.orp.simulator.event_bus.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class NewSessionEvent implements Event {

    private String sessionId;

    @Override public String getKey() {
        return sessionId;
    }
}

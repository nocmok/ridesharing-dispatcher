package com.nocmok.orp.simulator.event_bus.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class TicTac implements Event {

    private Long milliseconds;

    @Override public String getKey() {
        return "tictac";
    }
}

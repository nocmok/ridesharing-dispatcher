package com.nocmok.orp.simulator.event_sources;

import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_bus.event.TicTac;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class TicTacSource {

    private final EventBus eventBus;
    private final TicTac ticTac = TicTac.builder()
            .milliseconds(1000L)
            .build();

    @Autowired
    public TicTacSource(EventBus eventBus) {
        this.eventBus = eventBus;
    }

    @Scheduled(fixedRate = 1000)
    public void tictac() {
        eventBus.emit(ticTac);
    }
}

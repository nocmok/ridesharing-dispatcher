package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_bus.event.TicTac;
import com.nocmok.orp.simulator.service.telemetry.TelemetrySender;
import com.nocmok.orp.simulator.service.telemetry.WalkStrategy;
import com.nocmok.orp.simulator.service.telemetry.WalkingStub;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class VirtualDriver {

    private String sessionId;
    private EventBus eventBus;
    private TelemetrySender telemetrySender;
    private WalkStrategy walkStrategy;

    public VirtualDriver(String sessionId, EventBus eventBus, TelemetrySender telemetrySender) {
        this.sessionId = sessionId;
        this.eventBus = eventBus;
        this.telemetrySender = telemetrySender;

        this.walkStrategy = new WalkingStub(sessionId, 55.669213, 37.2826038, 55.6686797, 37.2831566, 30000);

        registerCallbacks();
    }

    private void registerCallbacks() {
        eventBus.subscribe(TicTac.class, this::sendTelemetry);
    }

    private void sendTelemetry(TicTac ticTac) {
        var telemetry = walkStrategy.nextTelemetry(ticTac.getMilliseconds());
        telemetrySender.sendTelemetry(telemetry);
    }
}

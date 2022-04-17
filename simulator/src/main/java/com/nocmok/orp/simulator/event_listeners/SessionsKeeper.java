package com.nocmok.orp.simulator.event_listeners;

import com.nocmok.orp.simulator.event_bus.EventBus;
import com.nocmok.orp.simulator.event_bus.event.NewSessionEvent;
import com.nocmok.orp.simulator.event_listeners.driver.VirtualDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class SessionsKeeper {

    private EventBus eventBus;
    private Function<String, VirtualDriver> vDriverFactory;

    private Map<String, VirtualDriver> virtualDrivers = new HashMap<>();

    @Autowired
    public SessionsKeeper(EventBus eventBus,
                          @Qualifier("virtualDriverFactory") Function<String, VirtualDriver> vDriverFactory) {
        this.eventBus = eventBus;
        this.vDriverFactory = vDriverFactory;

        registerCallbacks();
    }

    private void registerCallbacks() {
        eventBus.subscribe(NewSessionEvent.class, this::handleNewSession);
    }

    public void handleNewSession(NewSessionEvent event) {
        var newDriver = vDriverFactory.apply(event.getSessionId());
        virtualDrivers.put(event.getSessionId(), newDriver);
    }

    // TODO
    public void handleSessionDeath() {

    }
}

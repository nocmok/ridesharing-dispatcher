package com.nocmok.orp.simulator.event_bus;

import com.nocmok.orp.simulator.event_bus.event.Event;

@FunctionalInterface
public interface EventHandler<T extends Event> {

    void handle(T event);
}

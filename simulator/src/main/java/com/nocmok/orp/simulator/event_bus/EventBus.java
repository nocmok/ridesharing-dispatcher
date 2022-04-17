package com.nocmok.orp.simulator.event_bus;

import com.nocmok.orp.simulator.event_bus.event.Event;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventBus {

    private final Map<Class<? extends Event>, Map<String, List<EventHandler<Event>>>> handlers = new HashMap<>();
    private final Map<Class<? extends Event>, List<EventHandler<Event>>> broadCastHandlers = new HashMap<>();

    private <T extends Event> List<EventHandler<Event>> getHandlersForEvent(Event event) {
        return handlers.getOrDefault(event.getClass(), Collections.emptyMap())
                .getOrDefault(event.getKey(), Collections.emptyList());
    }

    private <T extends Event> List<EventHandler<Event>> getBroadcastHandlersForEvent(Event event) {
        return broadCastHandlers.getOrDefault(event.getClass(), Collections.emptyList());
    }

    public void emit(Event event) {
        getHandlersForEvent(event).forEach(handler -> handler.handle(event));
        getBroadcastHandlersForEvent(event).forEach(handlers -> handlers.handle(event));
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> eventType, String key, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, (k) -> new HashMap<>())
                .computeIfAbsent(key, (k) -> new ArrayList<>())
                .add((EventHandler<Event>) handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        broadCastHandlers.computeIfAbsent(eventType, (k) -> new ArrayList<>())
                .add((EventHandler<Event>) handler);
    }
}

package com.nocmok.orp.simulator.event_bus;

import com.nocmok.orp.simulator.event_bus.event.Event;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class EventBus {

    private final Map<Class<? extends Event>, Map<String, Collection<EventHandler<Event>>>> handlers = new ConcurrentHashMap<>();
    private final Map<Class<? extends Event>, Collection<EventHandler<Event>>> broadCastHandlers = new ConcurrentHashMap<>();

    private <T extends Event> Collection<EventHandler<Event>> getHandlersForEvent(Event event) {
        return handlers.getOrDefault(event.getClass(), Collections.emptyMap())
                .getOrDefault(event.getKey(), Collections.emptyList());
    }

    private <T extends Event> Collection<EventHandler<Event>> getBroadcastHandlersForEvent(Event event) {
        return broadCastHandlers.getOrDefault(event.getClass(), Collections.emptyList());
    }

    public void emit(Event event) {
        getHandlersForEvent(event).forEach(handler -> handler.handle(event));
        getBroadcastHandlersForEvent(event).forEach(handlers -> handlers.handle(event));
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> eventType, String key, EventHandler<T> handler) {
        handlers.computeIfAbsent(eventType, (k) -> new ConcurrentHashMap<>())
                .computeIfAbsent(key, (k) -> new ConcurrentLinkedQueue<EventHandler<Event>>())
                .add((EventHandler<Event>) handler);
    }

    @SuppressWarnings("unchecked")
    public <T extends Event> void subscribe(Class<T> eventType, EventHandler<T> handler) {
        broadCastHandlers.computeIfAbsent(eventType, (k) -> new ConcurrentLinkedQueue<>())
                .add((EventHandler<Event>) handler);
    }
}

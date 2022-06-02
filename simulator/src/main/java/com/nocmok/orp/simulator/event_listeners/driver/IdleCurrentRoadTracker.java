package com.nocmok.orp.simulator.event_listeners.driver;

import com.nocmok.orp.graph.api.Segment;

public class IdleCurrentRoadTracker implements CurrentRoadTracker {

    private Segment segment;

    public IdleCurrentRoadTracker(Segment segment) {
        this.segment = segment;
    }

    @Override public Segment getCurrentRoad() {
        return segment;
    }

    @Override public void updateCurrentRoad() {

    }
}

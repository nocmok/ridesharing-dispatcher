package com.nocmok.orp.simulator.event_bus.event;

import com.nocmok.orp.graph.api.Segment;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class RoadSegmentEntryEvent implements Event {

    private String sessionId;
    private Segment roadSegment;

    @Override public String getKey() {
        return sessionId;
    }
}

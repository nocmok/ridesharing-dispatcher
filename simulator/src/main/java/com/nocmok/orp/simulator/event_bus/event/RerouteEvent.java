package com.nocmok.orp.simulator.event_bus.event;

import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleEntry;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;

@AllArgsConstructor
@Builder
public class RerouteEvent implements Event {

    private String sessionId;
    private List<RouteNode> updatedRoute;
    private List<ScheduleEntry> updatedSchedule;

    public String getSessionId() {
        return sessionId;
    }

    public List<RouteNode> getUpdatedRoute() {
        return updatedRoute;
    }

    public List<ScheduleEntry> getUpdatedSchedule() {
        return updatedSchedule;
    }

    @Override public String getKey() {
        return sessionId;
    }
}

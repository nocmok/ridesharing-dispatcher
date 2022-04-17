package com.nocmok.orp.simulator.event_bus.event;

import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleNode;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
@Builder
public class RequestAssignConfirmationEvent implements Event {

    private String sessionId;
    private String serviceRequestId;
    private List<ScheduleNode> schedule;
    private List<RouteNode> routeScheduled;

    @Override public String getKey() {
        return sessionId;
    }
}

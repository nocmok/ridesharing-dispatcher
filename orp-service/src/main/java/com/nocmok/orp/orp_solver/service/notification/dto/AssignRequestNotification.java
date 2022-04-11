package com.nocmok.orp.orp_solver.service.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.solver.api.RouteNode;
import com.nocmok.orp.solver.api.ScheduleNode;
import lombok.Builder;

import java.util.List;

@Builder
public class AssignRequestNotification {

    @JsonProperty("sessionId")
    private String sessionId;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;

    @JsonProperty("schedule")
    private List<ScheduleNode> schedule;

    @JsonProperty("routeScheduled")
    private List<RouteNode> routeScheduled;

    public AssignRequestNotification(String sessionId, String serviceRequestId, List<ScheduleNode> schedule,
                                     List<RouteNode> routeScheduled) {
        this.sessionId = sessionId;
        this.serviceRequestId = serviceRequestId;
        this.schedule = schedule;
        this.routeScheduled = routeScheduled;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public List<ScheduleNode> getSchedule() {
        return schedule;
    }

    public List<RouteNode> getRouteScheduled() {
        return routeScheduled;
    }

    @Override public String toString() {
        return "AssignRequestNotification{" +
                "vehicleId='" + sessionId + '\'' +
                ", requestId='" + serviceRequestId + '\'' +
                ", schedule=" + schedule +
                ", routeScheduled=" + routeScheduled +
                '}';
    }
}

package com.nocmok.orp.orp_solver.service.notification.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;
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
    private List<GraphNode> routeScheduled;

    public AssignRequestNotification(String sessionId, String serviceRequestId, List<ScheduleNode> schedule,
                                     List<GraphNode> routeScheduled) {
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

    public List<GraphNode> getRouteScheduled() {
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
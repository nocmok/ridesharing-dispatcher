package com.nocmok.orp.kafka.orp_output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;
import lombok.Builder;

import java.util.List;

/**
 * Сообщение, которое отправляется диспетчером, когда тс приняло запрос на обработку и его план был перестроен.
 * Сообщение может использоваться:
 * 1) Для нотификации водителя о том, что его план перестроен
 * 2) Для нотификации клиента о том, что машина в пути
 */
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
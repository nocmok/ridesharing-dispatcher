package com.nocmok.orp.kafka.orp_output;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;

import java.util.List;

/**
 * Сообщение, которое отправляется
 */
public class ServiceRequestNotification {

    /**
     * Идентификатор тс которому будет отправлено уведомление
     */
    @JsonProperty("sessionId")
    private final String sessionId;

    /**
     * Идентификатор запроса с которым придет уведомление тс
     */
    @JsonProperty("requestId")
    private final String requestId;

    /**
     * Идентификатор резервации тс в рамках которой отправляется запрос на обслуживание.
     * <p>
     * При поступлении от тс подтверждение запроса проверяется резервация по этому идентификатору.
     * На тс реально назначается запрос, только если запись с резервацией не истекла
     */
    @JsonProperty("reservationId")
    private final String reservationId;

    @JsonProperty("augmentedSchedule")
    private final List<ScheduleNode> augmentedSchedule;

    @JsonProperty("augmentedRoute")
    private final List<GraphNode> augmentedRoute;

    public ServiceRequestNotification(String sessionId, String requestId, String reservationId,
                                      List<ScheduleNode> augmentedSchedule, List<GraphNode> augmentedRoute) {
        this.sessionId = sessionId;
        this.requestId = requestId;
        this.reservationId = reservationId;
        this.augmentedSchedule = augmentedSchedule;
        this.augmentedRoute = augmentedRoute;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public List<ScheduleNode> getAugmentedSchedule() {
        return augmentedSchedule;
    }

    public List<GraphNode> getAugmentedRoute() {
        return augmentedRoute;
    }

    @Override public String toString() {
        return "ServiceRequestNotificationDto{" +
                "vehicleId='" + sessionId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", augmentedSchedule=" + augmentedSchedule +
                ", augmentedRoute=" + augmentedRoute +
                '}';
    }
}
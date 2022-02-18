package com.nocmok.orp.orp_solver.service.notification.dto;

import com.nocmok.orp.core_api.GraphNode;
import com.nocmok.orp.core_api.ScheduleNode;

import java.util.List;

public class ServiceRequestNotification {

    /**
     * Идентификатор тс которому будет отправлено уведомление
     */
    private final String vehicleId;

    /**
     * Идентификатор запроса с которым придет уведомление тс
     */
    private final String requestId;

    /**
     * Идентификатор резервации тс в рамках которой отправляется запрос на обслуживание.
     * <p>
     * При поступлении от тс подтверждение запроса проверяется резервация по этому идентификатору.
     * На тс реально назначается запрос, только если запись с резервацией не истекла
     */
    private final String reservationId;

    private final List<ScheduleNode> augmentedSchedule;

    private final List<GraphNode> augmentedRoute;

    public ServiceRequestNotification(String vehicleId, String requestId, String reservationId,
                                      List<ScheduleNode> augmentedSchedule, List<GraphNode> augmentedRoute) {
        this.vehicleId = vehicleId;
        this.requestId = requestId;
        this.reservationId = reservationId;
        this.augmentedSchedule = augmentedSchedule;
        this.augmentedRoute = augmentedRoute;
    }

    public String getVehicleId() {
        return vehicleId;
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
                "vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                ", augmentedSchedule=" + augmentedSchedule +
                ", augmentedRoute=" + augmentedRoute +
                '}';
    }
}

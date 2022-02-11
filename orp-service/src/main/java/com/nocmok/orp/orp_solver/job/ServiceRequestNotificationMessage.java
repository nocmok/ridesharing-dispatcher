package com.nocmok.orp.orp_solver.job;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public class ServiceRequestNotificationMessage {

    @JsonProperty("vehicleId")
    private String vehicleId;

    @JsonProperty("requestId")
    private String requestId;

    @JsonProperty("reservationId")
    private String reservationId;

    public ServiceRequestNotificationMessage() {

    }

    public ServiceRequestNotificationMessage(String sessionId, String requestId, String reservationId) {
        this.vehicleId = sessionId;
        this.requestId = requestId;
        this.reservationId = reservationId;
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
}

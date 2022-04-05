package com.nocmok.orp.kafka.orp_input;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Подтверждение запроса от клиента, отправленное водителем
 */
public class AssignRequestMessage {

    @JsonProperty("reservationId")
    private String reservationId;

    @JsonProperty("vehicleId")
    private String vehicleId;

    @JsonProperty("serviceRequestId")
    private String serviceRequestId;

    public AssignRequestMessage(String reservationId, String vehicleId, String serviceRequestId) {
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.serviceRequestId = serviceRequestId;
    }

    public AssignRequestMessage() {

    }

    public String getReservationId() {
        return reservationId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    @Override public String toString() {
        return "AssignRequestMessage{" +
                "reservationId='" + reservationId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", requestId='" + serviceRequestId + '\'' +
                '}';
    }
}

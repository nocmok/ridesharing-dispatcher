package com.nocmok.orp.orp_solver.storage.notification;

import lombok.Builder;

@Builder
public class ServiceRequestOutboxEntry {

    private final String vehicleId;
    private final String requestId;
    private final String reservationId;

    public ServiceRequestOutboxEntry(String vehicleId, String requestId, String reservationId) {
        this.vehicleId = vehicleId;
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

    @Override public String toString() {
        return "ServiceRequestOutboxEntry{" +
                "vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                '}';
    }
}

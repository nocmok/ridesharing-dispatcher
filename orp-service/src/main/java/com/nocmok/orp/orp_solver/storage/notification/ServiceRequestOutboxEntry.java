package com.nocmok.orp.orp_solver.storage.notification;

import lombok.Builder;

import java.time.Instant;

@Builder
public class ServiceRequestOutboxEntry {

    private String vehicleId;
    private String requestId;
    private String reservationId;
    private Instant sentAt;

    public String getVehicleId() {
        return vehicleId;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getReservationId() {
        return reservationId;
    }

    public Instant getSentAt() {
        return sentAt;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public void setSentAt(Instant sentAt) {
        this.sentAt = sentAt;
    }

    @Override public String toString() {
        return "ServiceRequestOutboxEntry{" +
                "vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", reservationId='" + reservationId + '\'' +
                '}';
    }
}

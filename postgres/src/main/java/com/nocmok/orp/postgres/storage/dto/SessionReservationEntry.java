package com.nocmok.orp.postgres.storage.dto;

import lombok.Builder;

import java.time.Instant;

@Builder
public class SessionReservationEntry {

    private String reservationId;
    private String vehicleId;
    private String requestId;
    private Instant createdAt;
    private Instant expiredAt;

    public SessionReservationEntry() {
    }

    public SessionReservationEntry(String reservationId, String vehicleId, String requestId, Instant createdAt, Instant expiredAt) {
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.requestId = requestId;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public String getReservationId() {
        return reservationId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getRequestId() {
        return requestId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }
}

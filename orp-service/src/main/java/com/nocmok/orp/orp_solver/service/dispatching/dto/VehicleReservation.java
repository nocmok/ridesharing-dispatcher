package com.nocmok.orp.orp_solver.service.dispatching.dto;

import lombok.Builder;

import java.time.Instant;
import java.util.Objects;

@Builder
public class VehicleReservation {

    /**
     * Идентификатор резервирования
     */
    private String reservationId;

    /**
     * Идентификатор тс для резервирования
     */
    private String vehicleId;

    /**
     * Идентификатор запроса для которого резервируется тс
     */
    private String requestId;

    private Instant createdAt;

    private Instant expiredAt;

    public VehicleReservation(String reservationId, String vehicleId, String requestId, Instant createdAt, Instant expiredAt) {
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.requestId = requestId;
        this.createdAt = createdAt;
        this.expiredAt = expiredAt;
    }

    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public Instant getExpiredAt() {
        return expiredAt;
    }

    public void setExpiredAt(Instant expiredAt) {
        this.expiredAt = expiredAt;
    }

    @Override public String toString() {
        return "VehicleReservation{" +
                "reservationId='" + reservationId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                ", createdAt=" + createdAt +
                ", expiredAt=" + expiredAt +
                '}';
    }

    @Override public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        VehicleReservation that = (VehicleReservation) o;
        return Objects.equals(reservationId, that.reservationId);
    }

    @Override public int hashCode() {
        return Objects.hash(reservationId);
    }
}

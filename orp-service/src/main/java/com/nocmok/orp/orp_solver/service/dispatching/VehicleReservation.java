package com.nocmok.orp.orp_solver.service.dispatching;

import lombok.Builder;

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

    public VehicleReservation(String reservationId, String vehicleId, String requestId) {
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.requestId = requestId;
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

    @Override public String toString() {
        return "VehicleReservationTicketDto{" +
                "reservationId='" + reservationId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}

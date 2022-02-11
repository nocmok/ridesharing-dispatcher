package com.nocmok.orp.orp_solver.service.dispatching;

public class VehicleReservationTicketDto {

    /**
     * Идентификатор резервирования
     */
    private final String reservationId;

    /**
     * Идентификатор тс для резервирования
     */
    private final String vehicleId;

    /**
     * Идентификатор запроса для которого резервируется тс
     */
    private final String requestId;

    public VehicleReservationTicketDto(String reservationId, String vehicleId, String requestId) {
        this.reservationId = reservationId;
        this.vehicleId = vehicleId;
        this.requestId = requestId;
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

    @Override public String toString() {
        return "VehicleReservationTicketDto{" +
                "reservationId='" + reservationId + '\'' +
                ", vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}

package com.nocmok.orp.orp_solver.service.dto;

public class VehicleReservation {

    /**
     * Идентификатор тс для резервирования
     */
    private final String vehicleId;

    /**
     * Идентификатор запроса для которого резервируется тс
     */
    private final String requestId;

    public VehicleReservation(String vehicleId, String requestId) {
        this.vehicleId = vehicleId;
        this.requestId = requestId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getRequestId() {
        return requestId;
    }
}

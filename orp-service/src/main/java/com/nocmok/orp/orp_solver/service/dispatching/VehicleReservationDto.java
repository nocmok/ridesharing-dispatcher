package com.nocmok.orp.orp_solver.service.dispatching;

public class VehicleReservationDto {

    private final String vehicleId;

    private final String requestId;

    public VehicleReservationDto(String vehicleId, String requestId) {
        this.vehicleId = vehicleId;
        this.requestId = requestId;
    }

    public String getVehicleId() {
        return vehicleId;
    }

    public String getRequestId() {
        return requestId;
    }

    @Override public String toString() {
        return "VehicleReservationDto{" +
                "vehicleId='" + vehicleId + '\'' +
                ", requestId='" + requestId + '\'' +
                '}';
    }
}
